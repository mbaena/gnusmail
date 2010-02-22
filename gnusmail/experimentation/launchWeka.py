#coding=utf-8
"""
- En el directorio donde se ejecute este script, debe haber un acceso a weka (un vínculo, p.e.)
- En ~.gnusmail debe haber un acceso, llamdo maildir, a la carpeta raiz del dataset de enron (ln -s)
- Se supone que GNUSmailGoogle.jar esta en dist/, si no, crear vinculo o cambiar ruta (createDataSet y evaluateMOA)
"""
import commands, sys
import os
import os.path
import re
from threadpool import *

_GNUSMAIL_PATH=os.path.join("..", "dist")
_GNUSMAIL_SH=os.path.join(_GNUSMAIL_PATH, "gnusmail.sh")
_WEKA_JAR=os.path.join(_GNUSMAIL_PATH, "lib", "weka.jar")
_MAILDIR_PATH=os.path.join("dataset","maildir")
_OUTPUT_PATH="outputweka"

def createFilteringString(selector, nwords,author):
        output = "dataset" + selector + author + str(nwords) + ".arff"
        return """
java -cp %s weka.filters.supervised.attribute.AttributeSelection -E \"weka.attributeSelection.%s \" -S "weka.attributeSelection.Ranker -T -3.0E-4 -N %s" -c \"first" -i ~/.gnusmail/dataset.arff -o %s
        """ % (_WEKA_JAR, selector, nwords, output), output

def getAvailableSelectors():
        return ['GainRatioAttributeEval', 'ChiSquaredAttributeEval', 'ReliefFAttributeEval', 'InfoGainAttributeEval', 'OneRAttributeEval'][:1]

"""
dataset.arff will contain the attributes of @author
"""
def createDataSet(maildir, datasetfile):
	os.system('java -jar -Xmx5G %s -z%s -b%s' % (_GNUSMAIL_JAR, maildir, datasetfile))


def createWordlists(author):
	createDataSet(author)
	for nwords in [200,300,400]:
		selectors = getAvailableSelectors()
		for selector in selectors:
			print ('Selector ' + selector   + " num " + str(nwords))
			order, file = createFilteringString(selector,nwords,author)
			executeOrder(order)
			extractWordsFromArff(file)
			os.remove(file)
			print ('done')

def evaluateWeka(author, alg, output):
    global graficas
    maildir = os.path.join(_MAILDIR_PATH, author)
    task = """%s -z%s -i%s -e --weka-classifier=%s > %s.out""" % (_GNUSMAIL_SH, maildir, output, alg, output+"salidaporpantalla") #moaClassifier
    print 'Task', task
    commands.getoutput("bash %s" % (task))
    print 'Fin', task



def getAuthors():
	return ['beck-s','farmer-d','kaminski-v','kitchen-l','lokay-m','sanders-r','williams-w3']

def getWekaAlgorithms():
	return ["weka.classifiers.bayes.NaiveBayesUpdateable","weka.classifiers.lazy.IBk","weka.classifiers.rules.NNge"]

def imprimirgraficas(graficas):
    for (key, value) in graficas.iteritems():
        filePng = "grafica_" + key + ".png"
        print(filePng)
        sentencia = "plot "
        for gr in value:
            if not os.path.exists(output_file):
                continue
            sentencia += '"' + gr + '" w l' 
            if gr <> value[-1]:
                sentencia += ', '
        sentgnuplot = "echo 'set terminal png; set output \"" + filePng +  "\"; " + sentencia +  "' | gnuplot"
        print(sentgnuplot)
        os.system(sentgnuplot)

"""""""""
MAIN SECTION
"""""""""


#pool = ThreadPool(2)
if not os.path.exists(_OUTPUT_PATH):
    os.mkdir(_OUTPUT_PATH)
graficas = {}
for author in getAuthors()[4:5]:
    if not os.path.exists(os.path.join(_MAILDIR_PATH, author)):
        maildir = os.path.join(_MAILDIR_PATH, author)
        print "PATH NOT FOUND! %s " % (maildir)
        continue
    graficas[author] = []
    for alg in getWekaAlgorithms():
        output_file = os.path.join(_OUTPUT_PATH, 'ratesWeka' + author + alg.replace(" ","").replace("(","").replace(")",""))
        if os.path.exists(output_file):
            continue
	print(output_file)
        #pool.queueTask(evaluateWeka, (author, alg, output_file))
	evaluateWeka(author, alg, output_file)
        graficas[author].append(output_file)
#pool.joinAll()
imprimirgraficas(graficas)
