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
import math
import Queue
from string import Template

_GNUSMAIL_PATH=os.path.join("..", "dist")
_GNUSMAIL_SH=os.path.join(_GNUSMAIL_PATH, "gnusmail.sh")
_WEKA_JAR=os.path.join(_GNUSMAIL_PATH, "lib", "weka.jar")
_MAILDIR_PATH=os.path.join("dataset","maildir")
_OUTPUT_PATH="output"

#For graphics
accs = [] 

facts = 0

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
    global facts
    print task
    os.system("bash %s" % (task))
    facts += 1


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


def get_average_for_slidingwindow(num, window_size, count, accs):
  if len(accs) == 0: accs = [num]
  else: accs.append(num + accs[-1])
  factor_to_deduce = 0
  if count >= window_size: factor_to_deduce = accs[count-window_size]
  average = (accs[-1] - factor_to_deduce)/min(window_size, count+1)*1.0
  return average, accs

def get_average_for_fading_factors(num,fading_factor, Ss, Bs):
  if Bs == []: 
    Bs.append(1)
  else: 
    Bs.append(Bs[-1]*fading_factor + 1)
  if Ss == []: 
    Ss.append(num)
  else: Ss.append(Ss[-1]*fading_factor + num)
  res =  (Ss[-1]*1.0/Bs[-1], Ss, Bs)
  return res


def plot_matplotlib(file_in, method, param):
  accs = [] 
  Bs = []
  Ss = []
  count = 0
  averages = []
  f = open(file_in)
  for line in f.readlines():
    num = float(line)
    if method == "sliding-window":
      average, accs = get_average_for_slidingwindow(num, int(param), count, accs)
    else:
      (average, Ss, Bs) = get_average_for_fading_factors(num, float(param), Ss, Bs)
    averages.append(average)
    count += 1
  plot(averages, label=file_in, linewidth=1)
  return(averages)

def get_mcnemar_points_slidingwindow(file_in1, file_in2, window_size):
  q1 = Queue.Queue(int(window_size))
  q2 = Queue.Queue(int(window_size))
  sign = lambda x: math.copysign(1, x)
  f1 = open(file_in1)
  f2 = open(file_in2)
  nums = []
  n01 = 0
  n10 = 0
  mcnemars = []
  while True:
    try:
      n1 = float(f1.next())
      n2 = float(f2.next())

      #Queue to manage time window. When an item is too old, is deleted
      item_to_deduce1 = item_to_deduce2 = 0
      if q1.full(): 
        item_to_deduce1 = q1.get()
      q1.put(n1)
      if q2.full(): 
        item_to_deduce2 = q2.get()
      q2.put(n2)
      if item_to_deduce1 == 0 and item_to_deduce2 == 1: 
        n01 -= 1
      elif item_to_deduce1 == 1 and item_to_deduce2 == 0: 
        n10 -= 1

      if n1 == 0 and n2 == 1: n01 += 1
      elif n1 == 1 and n2 == 0: n10 += 1
      mcnemar = 0
      if (n01+n10) > 0: mcnemar = 1.0 * sign(n01-n10)  * ((n01-n10) ** 2) / (n01+n10)
      mcnemars.append(mcnemar)
    except:  #End of iteration
      break
  return mcnemars

def get_mcnemar_points_fadingfactors_v1(file_in1, file_in2, factor):
  """
  First version of the McNemar measure with fading factors
  """
  #First, MnNemar points as calculated by original formula
  sign = lambda x: math.copysign(1, x)
  f1 = open(file_in1)
  f2 = open(file_in2)
  nums = []
  n01  = n10 = 0
  mcnemars = []
  while True:
    try:
      n1 = float(f1.next())
      n2 = float(f2.next())
      if n1 == 0 and n2 == 1: n01 += 1
      elif n1 == 1 and n2 == 0: n10 += 1
      mcnemar = 0
      if (n01+n10) > 0: mcnemar = 1.0 * sign(n01-n10)  * ((n01-n10) ** 2) / (n01+n10)
      mcnemars.append(mcnemar)
    except: 
      break

  #Then, fading factors, using S_i = M_i + \alpha S_{i-1], B_i = N_i + \alpha B_{i-1} and Ej = Sj / Bj
  res = []
  Sj = 0
  Bj = 0
  for i in range(1, len(mcnemars)):
    vector = mcnemars[0:i]
    Sj = vector[-1] + (Sj*factor)
    Bj = i +1 + (Bj*factor)
    Ej = Sj / Bj
    res.append(Ej)
  return res

def get_mcnemar_points_fadingfactors_v2(file_in1, file_in2, factor):
  """
  Second version of the McNemar measure with fading factors.
  Short explation: M = sign(n01-n10)\frac{(n01-n10)P^2}{n01-n10}
  Let f01_i == 0 for a point i if classifier A misclassifies and B classifies well. 
  Similar with f10.
  Then, rewriting n10_i = n10{i-1} + f10_i (and similarly with n01_i}:
  M = sign (n01_{i-1} + f01_i - n10{i-1} - f10_i) \frac{(n01_{i-1} + f01_i - n10{i-1} - f10_i)^2 }{(n01_{i-1} + f01_i + n10{i-1} + f10_i) }

  The idea is to apply fading factor \alpha to the n10_{i-1} and n01_{i-1}:
  M = sign (\alpha n01_{i-1} + f01_i -\alpha  n10{i-1} - f10_i) \frac{(\alpha n01_{i-1} + f01_i - \alpha n10{i-1} - f10_i)^2 }{(\alpha n01_{i-1} + f01_i + \alpha n10{i-1} + f10_i) }
  """
  sign = lambda x: math.copysign(1, x)
  f1 = open(file_in1)
  f2 = open(file_in2)
  nums = []
  n01 = 0
  n10 = 0
  mcnemars = []
  while True:
    try:
      n1 = float(f1.next())
      n2 = float(f2.next())
      update01 = 0
      update10 = 0
      if n1 == 0 and n2 == 1: update01 = 1
      elif n1 == 1 and n2 == 0: update10 = 1
      n01 = update01 + (factor*n01)
      n10 = update10 + (factor*n10)
      mcnemar = 0
      if (n01+n10) > 0: mcnemar = 1.0 * sign(n01-n10)  * ((n01-n10) ** 2) / (n01+n10)
      mcnemars.append(mcnemar)
    except: 
      break
  return mcnemars

def get_mcnemar_points(file_in1, file_in2, method, param):
  if method == "sliding-window":
    return get_mcnemar_points_slidingwindow(file_in1, file_in2, param)
  if method == "fading-factor":
    return get_mcnemar_points_fadingfactors_v1(file_in1, file_in2, float(param))
  else: print("Method unknown " + method)

#def add_cdrift_to_graphic(cdrift):
#  state = "SearchFolder"
#  v = []
#  f = open(cdrift[0])
#  for line in f.readlines():
#    if state == "SearchFolder":
#      if line.startswith("Folder: "): 
#        found = False
#        foundWarning = False
#        state = "SearchCD"
#    elif state == "SearchCD":
#      if line.startswith("0 1"): 
#        found = True
#      elif line.startswith("1 0"):
#        foundWarning = True
#      elif line.startswith("Folder: "):
#        if found: v.append(1.0)
#        #elif foundWarning: v.append(0.5)
#        else: v.append(0)
#        found = False
#        foundWarning = False
#  f.close()
#  plot(v, linestyle="--", linewidth=.5)

def add_cdrift_to_graphic(cdrift):
  class CDriftAccumulator(object): 

    def __init__(self):
      self.changes_in_state = []

    def add_state(self, point, state):
      if self.changes_in_state == [] or self.changes_in_state[-1][1] != state:
        self.changes_in_state.append((point, state))

    def search_cd(self):
      concept_drifts = []
      for i in range(0, len(self.changes_in_state)-1):
        if self.changes_in_state[i][-1] == "W" and self.changes_in_state[i+1][-1] == "D":
          concept_drifts.append(self.changes_in_state[i][0])
      return concept_drifts

    def __str__(self):
      return (str(self.changes_in_state))
      

  state = "SearchFolder"
  v = []
  f = open(cdrift[0])
  counter = 0
  counter_documents = 0
  counter_classifier = 0
  last_warning = -1
  cdaccumulators = []
  for i in range(0, 10): cdaccumulators.append(CDriftAccumulator())
  for line in f.readlines():
    if state == "SearchFolder":
      if line.startswith("Folder: "): 
        counter_documents += 1
        found = False
        foundWarning = False
        state = "SearchCD"
    elif state == "SearchCD":
      if line.startswith("0 1"): 
        cdaccumulators[counter_classifier].add_state(counter_documents, "D") #Drift
        counter_classifier += 1
      elif line.startswith("1 0"):
        cdaccumulators[counter_classifier].add_state(counter_documents, "W") #Warning
        counter_classifier += 1
      elif line.startswith("0 0"):
        cdaccumulators[counter_classifier].add_state(counter_documents, "I") #Inline
        counter_classifier += 1
      elif line.startswith("Folder: "):
        counter_classifier = 0
        counter_documents += 1
    counter += 1
  f.close()
  lim = 40
  all_changes = []
  reduced_changes = []
  for c in cdaccumulators:
    all_changes += c.search_cd()
  for c in all_changes:
    if reduced_changes == [] or c - reduced_changes[-1] > lim: reduced_changes.append(c)
  for i in range(0, counter_documents): 
    if i in reduced_changes: v.append(1)
    else: v.append(0)
  plot(v, linestyle="--", linewidth=.5)

def print_graphics(graphs, cdrifts, method_prequential, param):
    for author in graphs.keys():
      performances_per_author = []
      filename = "graph_" + author + ".pdf"
      for graph in graphs[author]:
        performance = plot_matplotlib(graph, method_prequential, param)
        performances_per_author.append(performance)
        add_cdrift_to_graphic(cdrifts[author])

      fp = FontProperties(size=6)
      legend(loc=0, prop=fp, title="leyenda")
      savefig(filename)
      clf()

def print_mcnemar(graphs, method, param):
  def pairs(vector):
    print(vector)
    res = []
    for i in range(0, len(vector)):
      for j in range(0, len(vector)):
        if i != j: res.append((vector[i],vector[j]))
    return res
  for author in graphs.keys():
    graph_pairs = pairs(graphs[author])
    for pair in graph_pairs:
      mcnemar = get_mcnemar_points(pair[0], pair[1], method, param)
      axhline(6.635) #McNemar critical point
      plot(mcnemar)
      filename = "mcnemar_%s_%s_%s_%s_%s.pdf" % (author,method, param, pair[0].split("/")[-1], pair[1].split("/")[-1])
      savefig(filename)
      clf()

def launchEvaluation(evaluation_method, prefix, algorithms, method, param, method_mcnemar, param_mcnemar):
    global facts
    pool = ThreadPool(4)
    if not os.path.exists(_OUTPUT_PATH):
        os.mkdir(_OUTPUT_PATH)
    if not os.path.exists(_MAILDIR_PATH):
        print "Enron Dataset not found in %s!" % (_MAILDIR_PATH,)
        get_enron_dataset()
    graphs = {}
    cdrifts = {}
    purge_pat = re.compile(r"[^\w]")
    for author in getAuthors():
        if not os.path.exists(os.path.join(_MAILDIR_PATH, author)):
            maildir = os.path.join(_MAILDIR_PATH, author)
            logging.info("PATH NOT FOUND! %s " % (maildir))
            continue
        graphs[author] = []
        cdrifts[author] = []
        for alg in algorithms:
            output_file = os.path.join(_OUTPUT_PATH, "%s_%s_%s" % (prefix, author, purge_pat.sub("", alg)))
            graphs[author].append(output_file)
            if "SingleClassifierDrift" in output_file: 
              cdrifts[author].append(output_file + ".out")
            else:
              print("No " + output_file)
            if os.path.exists(output_file):
                logging.info("PATH EXISTS ! %s. We do not launch experimentation for it " % (output_file))
                continue
            print output_file
            pool.queueTask(evaluation_method, (author, alg, output_file))
            #evaluation_method(author, alg, output_file)
    pool.joinAll()
    print_graphics(graphs, cdrifts, method, param)
    #print_mcnemar(graphs, method_mcnemar, param_mcnemar)

"""""""""
MAIN SECTION
"""""""""

errormessage = "Usage: python " + sys.argv[0] + " moa|weka sliding-window|fading-factor param sliding-window|fading-factor param"
if len(sys.argv) < 6:
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
method_mcnemar = sys.argv[4]
param_mcnemar = sys.argv[5]

launchEvaluation(evaluation_method, prefix, algs, method, param, method_mcnemar, param_mcnemar)

