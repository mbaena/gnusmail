#coding=utf-8
import commands, sys
import os
import os.path
import re
from threadpool import *
import logging
from time import sleep
import urllib
from string import Template

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
    task = """%s -z%s -m%s --moa-classifier=\\\"%s\\\" > %s.out""" % (_GNUSMAIL_SH, maildir, output, alg, output)
    genericEvaluation(task)

def getAuthors():
	return ['beck-s', 'kaminski-v', 'kitchen-l', 'lokay-m','sanders-r','williams-w3', 'farmer-d']

def getMoaAlgorithms():
	return ["MajorityClass", 
        """HoeffdingTreeNBAdaptive -g 1""",
#        """HoeffdingTreeNBAdaptive -g 1 -c .05""",
#        """HoeffdingTreeNBAdaptive -g 1 -c .1""",
#        """HoeffdingTreeNBAdaptive -g 1 -c .2""",
#        """HoeffdingTreeNBAdaptive -g 1 -c .3""",
#        """HoeffdingTreeNBAdaptive -g 1 -c .4""",
#        """HoeffdingTreeNBAdaptive -g 1 -c .1 -b""",
#        """HoeffdingTreeNBAdaptive -g 1 -c .4 -b""",
        """WEKAClassifier -l weka.classifiers.rules.NNge""",
#        """OzaBag -l \(WEKAClassifier -l weka.classifiers.rules.NNge\) -s 10""",
        """OzaBag -l \(SingleClassifierDrift -d DDM -l \(WEKAClassifier -l weka.classifiers.rules.NNge\)\) -s 10""",
#        """OzaBag -l \(SingleClassifierDrift -d EDDM -l \(WEKAClassifier -l weka.classifiers.rules.NNge\)\) -s 10""",
#        """OzaBagAdwin -l \(WEKAClassifier -l weka.classifiers.rules.NNge\) -s 10""",
#        """OzaBagAdwin -l \(HoeffdingTreeNBAdaptive -g 1 -c .1 -b\) -s 10""",
#        """SingleClassifierDrift -d DDM -l \(WEKAClassifier -l weka.classifiers.rules.NNge\)""",
#        """SingleClassifierDrift -d DDM -l \(HoeffdingTreeNBAdaptive -g 1 -c .1\)""",
#        """SingleClassifierDrift -d DDM -l \(OzaBag -l \(WEKAClassifier -l weka.classifiers.rules.NNge\)\)""",
#        """SingleClassifierDrift -d DDM -l \(OzaBagAdwin -l \(WEKAClassifier -l weka.classifiers.rules.NNge\)\)""",
#        """SingleClassifierDrift -d DDM -l \(OzaBagAdwin -l \(SingleClassifierDrift -d DDM -l \(WEKAClassifier -l weka.classifiers.rules.NNge\)\)\)""",
#        """SingleClassifierDrift -d EDDM -l \(WEKAClassifier -l weka.classifiers.rules.NNge\)"""
#        """SingleClassifierDrift -d EDDM -l \(HoeffdingTreeNBAdaptive -g 1 -c .1\)"""
#        """SingleClassifierDrift -d EDDM -l \(OzaBag -l \(HoeffdingTreeNBAdaptive -g 1 -c .1\)\)""",
         ]

def getWekaAlgorithms():
	return [
"weka.classifiers.bayes.NaiveBayesUpdateable",
        "weka.classifiers.lazy.IBk",
        "weka.classifiers.rules.NNge"]

def do_gnuplot(fname, plotdata, title, ylabel, xlabel):
    f = open(fname + ".gnuplot", "w")
    out = Template("""
set terminal epslatex monochrome 8
set output "${filename}.eps"
reset
set size 0.8,0.8
set yrange [0:100]  
set title $title
set ylabel $ylabel
set xlabel $xlabel
set style fill pattern 8
plot $plotdata
set terminal png
set output "${filename}.png"
reset
set size 0.8,0.8
set yrange [0:100]  
set title $title
set ylabel $ylabel
set xlabel $xlabel
set style fill pattern 8
plot $plotdata
set output
""")
    out = out.substitute(filename=fname, plotdata=plotdata,title=title, ylabel=ylabel, xlabel=xlabel)
    f.write(out)
    f.close()
    commands.getstatusoutput('gnuplot ' + fname + '.gnuplot')
    commands.getstatusoutput('epstopdf ' + fname + '.eps')

def imprimirgraficas(graficas):
    for (key, value) in graficas.iteritems():
        filename = "grafica_" + key
        sentencia = ""
        classifier_num = 1
        for gr in value:
            sentencia += '"' + gr + '" t "\\M#%s" w l'  % classifier_num
            classifier_num += 1
            if gr <> value[-1]:
                sentencia += ', '
        print sentencia
        do_gnuplot(os.path.join(_OUTPUT_PATH, filename), sentencia, "", "", "")


def launchEvaluation(evaluation_method, prefix, algorithms):
    global hechos
    pool = ThreadPool(4)
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
            graficas[author].append(output_file)
            if os.path.exists(output_file):
                logging.info("PATH EXISTS ! %s. We do not launch experimentation for it " % (output_file))
                continue
            print output_file
            pool.queueTask(evaluation_method, (author, alg, output_file))
            #evaluation_method(author, alg, output_file)
    pool.joinAll()
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

