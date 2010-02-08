#coding=utf-8
import commands, sys
import os
import os.path
import re
from threadpool import *

_GNUSMAIL_PATH=os.path.join("..", "dist")
_GNUSMAIL_SH=os.path.join(_GNUSMAIL_PATH, "gnusmail.sh")
_WEKA_JAR=os.path.join(_GNUSMAIL_PATH, "lib", "weka.jar")
_MAILDIR_PATH=os.path.join("dataset","maildir")
_OUTPUT_PATH="output"

def genericEvaluation(task):
    print task
    commands.getoutput("bash %s" % (task))

def evaluateWeka(author, alg, output):
    maildir = os.path.join(_MAILDIR_PATH, author)
    task = """%s -z%s -i%s -e --weka-classifier=%s > %s.out""" % (_GNUSMAIL_SH, maildir, output, alg, output+"salidaporpantalla") #moaClassifier
    genericEvaluation(task)

def evaluateMOA(author, alg, output):
    maildir = os.path.join(_MAILDIR_PATH, author)
    task = """%s -z%s -m%s.rates --moa-classifier=\\\"%s\\\" > %s.out""" % (_GNUSMAIL_SH, maildir, output, alg, output)
    genericEvaluation(task)

def getAuthors():
	return ['kitchen-l', 'lokay-m','sanders-r','beck-s','williams-w3', 'farmer-d', 'kaminski-v'][-1:]

def getMoaAlgorithms():
	return ["MajorityClass", 
        "HoeffdingTreeNBAdaptive",
        "SingleClassifierDrift -d DDM -l HoeffdingTreeNBAdaptive", 
        "SingleClassifierDrift -d EDDM -l HoeffdingTreeNBAdaptive", 
        "OzaBagAdwin -l HoeffdingTreeNBAdaptive -s 10",
        "SingleClassifierDrift -d DDM -l (OzaBag -l HoeffdingTreeNBAdaptive)"]

def getWekaAlgorithms():
	return ["weka.classifiers.bayes.NaiveBayesUpdateable",
        "weka.classifiers.lazy.IBk",
        "weka.classifiers.rules.NNge"]

def imprimirgraficas(graficas):
    for (key, value) in graficas.iteritems():
        filePng = "grafica_" + key + ".png"
        sentencia = "plot "
        for gr in value:
            if not os.path.exists(output_file):
                continue
            sentencia += '"' + gr + '" w l' 
            if gr <> value[-1]:
                sentencia += ', '
        print sentencia
        sentgnuplot = "echo 'set terminal png; set output \"" + filePng +  "\"; " + sentencia +  "' | gnuplot"
        os.system(sentgnuplot)


def launchEvaluation(evaluation_method, prefix):
    pool = ThreadPool(2)
    if not os.path.exists(_OUTPUT_PATH):
        os.mkdir(_OUTPUT_PATH)
    graficas = {}
    for author in getAuthors():
        if not os.path.exists(os.path.join(_MAILDIR_PATH, author)):
            maildir = os.path.join(_MAILDIR_PATH, author)
            print "PATH NOT FOUND! %s " % (maildir)
            continue
        graficas[author] = []
        for alg in getMoaAlgorithms():
            output_file = os.path.join(_OUTPUT_PATH, prefix + author + alg.replace(" ","").replace("(","").replace(")",""))
            if os.path.exists(output_file):
                continue
            print output_file
            pool.queueTask(evaluation_method, (author, alg, output_file))
            graficas[author].append(output_file)
    pool.joinAll()
    #imprimirgraficas(graficas)
"""""""""
MAIN SECTION
"""""""""

errormessage = "Usage: python " + sys.argv[0] + "moa | weka"
if len(sys.argv) < 2:
    print(errormessage)
    exit()
param = sys.argv[1].lower()
if not param in ['moa', 'weka']:
    print(errormessage)
    exit()
elif param == 'moa':
    evaluation_method = evaluateMOA
    prefix = 'ratesMoa'
else:
    evaluation_method = evaluateWeka
    prefix = 'ratesWeka'
launchEvaluation(evaluation_method, prefix)

