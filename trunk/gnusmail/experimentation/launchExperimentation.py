#coding=utf-8
import commands, sys
import os
import os.path
import re
from threadpool import *
import logging
from time import sleep
import urllib
from matplotlib.pyplot import *
from matplotlib.font_manager import FontProperties
from string import Template

_GNUSMAIL_PATH=os.path.join("..", "dist")
_GNUSMAIL_SH=os.path.join(_GNUSMAIL_PATH, "gnusmail.sh")
_WEKA_JAR=os.path.join(_GNUSMAIL_PATH, "lib", "weka.jar")
_MAILDIR_PATH=os.path.join("dataset","maildir")
_OUTPUT_PATH="output"

#For graphics
accs = [] #for 
Bs = []
Ss = []

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

def apply_cochram_test(matrix):
  pass

def evaluateWeka(author, alg, output):
    maildir = os.path.join(_MAILDIR_PATH, author)
    task = """%s -z%s -i%s -e --weka-classifier=%s > %s.cdrifts""" % (_GNUSMAIL_SH, maildir, output, alg, output+"salidaporpantalla") #moaClassifier
    print(task)
    genericEvaluation(task)

def evaluateMOA(author, alg, output):
    maildir = os.path.join(_MAILDIR_PATH, author)
    task = """%s -z%s -m%s --moa-classifier=\\\"%s\\\" > %s.out""" % (_GNUSMAIL_SH, maildir, output, alg, output)
    genericEvaluation(task)

def getAuthors():
	#return ['beck-s', 'kaminski-v', 'kitchen-l', 'lokay-m','sanders-r','williams-w3', 'farmer-d'][-2:-1]
	return ['beck-s']

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


def get_average_for_slidingwindow(num, window_size, count, accs):
  if len(accs) == 0: accs = [num]
  else: accs.append(num + accs[-1])
  factor_to_deduce = 0
  if count >= window_size: factor_to_deduce = accs[count-window_size]
  average = (accs[-1] - factor_to_deduce)/min(window_size, count+1)*1.0
  return average, accs

def get_average_for_fading_factors(num,fading_factor, Bs, Ss):
  if Bs == []: Bs.append(1)
  else: Bs.append(Bs[-1]*fading_factor + 1)
  if Ss == []: Ss.append(num)
  else: Ss.append(Ss[-1]*fading_factor + num)
  return Ss[-1]*1.0/Bs[-1], Ss, Bs


def plot_matplotlib(file_in, method, param):
  print("MATPLOTLIB")
  accs = Bs = Ss = []
  count = 0
  averages = []
  f = open(file_in)
  for line in f.readlines():
    num = float(line)
    if method == "sliding-window":
      average, accs = get_average_for_slidingwindow(num, param, count, accs)
    else:
      average , Bs, Ss = get_average_for_fading_factors(num, param, Bs, Ss)
    averages.append(average)
    count += 1
  plot(averages, label=file_in, linewidth=1)
  return(averages)

def add_cdrift_to_graphic(cdrift):
  state = "BuscarFolder"
  v = []
  f = open(cdrift[0])
  for line in f.readlines():
    if state == "BuscarFolder":
      if line.startswith("Folder: "): 
        found = False
        foundWarning = False
        state = "BuscarCD"
    elif state == "BuscarCD":
      if line.startswith("0 1"): 
        found = True
      elif line.startswith("1 0"):
        foundWarning = True
      elif line.startswith("Folder: "):
        if found: v.append(1.0)
        #elif foundWarning: v.append(0.5)
        else: v.append(0)
        found = False
        foundWarning = False
  f.close()
  plot(v, linestyle="--", linewidth=.5)

def imprimirgraficas(graficas, cdrifts, method, param):
    for author in graficas.keys():
      performances_per_author = []
      filename = "grafica_" + author + ".pdf"
      for graph in graficas[author]:
        performance = plot_matplotlib(graph, method, param)
        performances_per_author.append(performance)
        add_cdrift_to_graphic(cdrifts[author])

      fp = FontProperties(size=6)
      legend(loc=0, prop=fp, title="leyenda")
      savefig(filename + ".pdf")
      apply_cochram_test(performances_per_author)


def launchEvaluation(evaluation_method, prefix, algorithms, method, param):
    global hechos
    pool = ThreadPool(4)
    if not os.path.exists(_OUTPUT_PATH):
        os.mkdir(_OUTPUT_PATH)
    if not os.path.exists(_MAILDIR_PATH):
        print "Enron Dataset not found in %s!" % (_MAILDIR_PATH,)
        get_enron_dataset()
    graficas = {}
    cdrifts = {}
    purge_pat = re.compile(r"[^\w]")
    for author in getAuthors():
        if not os.path.exists(os.path.join(_MAILDIR_PATH, author)):
            maildir = os.path.join(_MAILDIR_PATH, author)
            logging.info("PATH NOT FOUND! %s " % (maildir))
            continue
        graficas[author] = []
        cdrifts[author] = []
        for alg in algorithms:
            output_file = os.path.join(_OUTPUT_PATH, "%s_%s_%s" % (prefix, author, purge_pat.sub("", alg)))
            graficas[author].append(output_file)
            if "SingleClassifierDrift" in output_file: 
              cdrifts[author].append(output_file + ".cdrifts")
            else:
              print("No " + output_file)
            if os.path.exists(output_file):
                logging.info("PATH EXISTS ! %s. We do not launch experimentation for it " % (output_file))
                continue
            print output_file
            pool.queueTask(evaluation_method, (author, alg, output_file))
            #evaluation_method(author, alg, output_file)
    pool.joinAll()
    print("Hecho, a imprimir graficas")
    imprimirgraficas(graficas, cdrifts, method, param)

"""""""""
MAIN SECTION
"""""""""

errormessage = "Usage: python " + sys.argv[0] + " moa|weka sliding-window|fading-factor param"
if len(sys.argv) < 4:
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
method = sys.argv[2]
param = sys.argv[3]

launchEvaluation(evaluation_method, prefix, algs, method, param)

