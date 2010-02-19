#coding=utf-8
import commands, sys
import os
import os.path
import re
from threadpool import *
import logging
from time import sleep
import urllib

_GNUSMAIL_PATH=os.path.join("..", "dist")
_GNUSMAIL_SH=os.path.join(_GNUSMAIL_PATH, "gnusmail.sh")
_WEKA_JAR=os.path.join(_GNUSMAIL_PATH, "lib", "weka.jar")
_MAILDIR_PATH=os.path.join("dataset","maildir")
_OUTPUT_PATH="output"

hechos = 0

def get_enron_dataset():
    import tarfile
    url = "http://www.cs.cmu.edu/~enron/enron_mail_082109.tar.gz"
    x = raw_input("Do you want to download Enron Email Dataset? (y)/n")
    if not (x=='' or x=='y'):
       exit()
    if not os.path.exists("dataset"):
        os.mkdir("dataset")
    if not os.path.exists(_MAILDIR_PATH):
        os.mkdir(_MAILDIR_PATH)
    #urllib.urlretrieve(url, "dataset/enron_mail_082109.tar.gz")
    os.system("wget -O dataset/enron_mail_082109.tar.gz " + url)
    tar = tarfile.open("dataset/enron_mail_082109.tar.gz")
    tar.extractall("dataset")
    tar.close()

def genericEvaluation(task):
    global hechos
    print task
    os.system("bash %s" % (task))
    hechos += 1

def evaluateWeka(author, alg, output):
    maildir = os.path.join(_MAILDIR_PATH, author)
    task = """%s -z%s -i%s -e --weka-classifier=%s > %s.out""" % (_GNUSMAIL_SH, maildir, output, alg, output+"salidaporpantalla") #moaClassifier
    genericEvaluation(task)

def evaluateMOA(author, alg, output):
    maildir = os.path.join(_MAILDIR_PATH, author)
    task = """%s -z%s -m%s.rates --moa-classifier=\\\"%s\\\" > %s.out""" % (_GNUSMAIL_SH, maildir, output, alg, output)
    genericEvaluation(task)

def getAuthors():
	return ['beck-s', 'kaminski-v', 'kitchen-l', 'lokay-m','sanders-r','williams-w3', 'farmer-d']

def getMoaAlgorithms():
	return ["MajorityClass", 
        "HoeffdingTreeNBAdaptive",
        "SingleClassifierDrift -d DDM -l HoeffdingTreeNBAdaptive", 
        "SingleClassifierDrift -d EDDM -l HoeffdingTreeNBAdaptive", 
        "OzaBagAdwin -l HoeffdingTreeNBAdaptive -s 10",
        "SingleClassifierDrift -d DDM -l \\(OzaBag -l HoeffdingTreeNBAdaptive\\)"][-1:]

def getWekaAlgorithms():
	return ["weka.classifiers.bayes.NaiveBayesUpdateable",
        "weka.classifiers.lazy.IBk",
        "weka.classifiers.rules.NNge"]

def imprimirgraficas(graficas):
    for (key, value) in graficas.iteritems():
        filePng = "grafica_" + key + ".png"
        sentencia = "plot "
        for gr in value:
            #if not os.path.exists(output_file):
            #     logging.info(output_file + " does not exist; ignoring")
            #     continue
            sentencia += '"' + gr + '" w l' 
            if gr <> value[-1]:
                sentencia += ', '
        print sentencia
        sentgnuplot = "echo 'set terminal png; set output \"" + filePng +  "\"; " + sentencia +  "' | gnuplot"
        os.system(sentgnuplot)


def launchEvaluation(evaluation_method, prefix, algorithms):
    global hechos
    #pool = ThreadPool(3)
    if not os.path.exists(_OUTPUT_PATH):
        os.mkdir(_OUTPUT_PATH)
    if not os.path.exists(_MAILDIR_PATH):
        print "Enron Dataset not found!"
        get_enron_dataset()
    graficas = {}
    purge_pat = re.compile(r"[^\w]")
    for author in getAuthors():
        if not os.path.exists(os.path.join(_MAILDIR_PATH, author)):
            maildir = os.path.join(_MAILDIR_PATH, author)
            logging.info("PATH NOT FOUND! %s " % (maildir))
            continue
        graficas[author] = []
        for alg in algorithms:
            output_file = os.path.join(_OUTPUT_PATH, "%s_%s_%s" % (prefix, author, purge_pat.sub("", alg)))
            if os.path.exists(output_file):
                logging.info("PATH EXISTS ! %s. We do not launch experimentation for it " % (maildir))
                continue
            print output_file
            #pool.queueTask(evaluation_method, (author, alg, output_file))
            evaluation_method(author, alg, output_file)
            graficas[author].append(output_file)
    #pool.joinAll()
    print("Hecho, a imprimir graficas")
    imprimirgraficas(graficas)

"""""""""
MAIN SECTION
"""""""""

errormessage = "Usage: python " + sys.argv[0] + " moa | weka"
if len(sys.argv) < 2:
    print(errormessage)
    exit()
param = sys.argv[1].lower()
if not param in ['moa', 'weka']:
    print(errormessage)
    exit()
elif param == 'moa':
    evaluation_method = evaluateMOA
    algs = getMoaAlgorithms()
    prefix = 'ratesMoa'
else:
    evaluation_method = evaluateWeka
    algs = getWekaAlgorithms()
    prefix = 'ratesWeka'

launchEvaluation(evaluation_method, prefix, algs)

