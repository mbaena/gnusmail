#coding=utf-8
"""
- En el directorio donde se ejecute este script, debe haber un acceso a weka (un vínculo, p.e.)
- En ~.gnusmail debe haber un acceso, llamdo maildir, a la carpeta raiz del dataset de enron (ln -s)
"""

import os

graficas = []

def createLink(author):
	order = """
rm ~/.gnusmail/maildirln ; ln -s ~/.gnusmail/maildir/%s ~/.gnusmail/maildirln
	""" % (author)
	print(order)
	os.system(order)

def createFilteringString(selector, nwords,author):
        output = "dataset" + selector + author + str(nwords) + ".arff"
        return """
java -cp weka.jar weka.filters.supervised.attribute.AttributeSelection -E \"weka.attributeSelection.%s \" -S "weka.attributeSelection.Ranker -T -3.0E-4 -N %s" -c \"first" -i ~/.gnusmail/dataset.arff -o %s
        """ % (selector, nwords, output), output

def executeOrder(order):
        os.system(order)

def getAvailableSelectors():
        return ['GainRatioAttributeEval', 'ChiSquaredAttributeEval', 'ReliefFAttributeEval', 'InfoGainAttributeEval', 'OneRAttributeEval'][:1]

def extractWordsFromArff(file):
        f = open(file)
        lines = [line for line in f.readlines() if line.startswith('@attribute')]
        words = [line.split()[1] for line in lines]
        f.close()
        wordsfile = file.replace('arff','list')
        f = open("wordlists/" + wordsfile,"w")
        for word in words:
		if word[0].islower():
			f.write(word + '\n')
        f.close()

"""
dataset.arff will contain the attributes of @author
"""
def createDataSet(author):
	createLink(author)
	os.system('rm ~/.gnusmail/atributos.csv')
	os.system('rm ~/.gnusmail/dataset.arff')
	os.system('rm ~/.gnusmail/model.bin')
	os.system('java -jar -Xmx5G dist/GNUSMailGoogle.jar -z -b')
	os.system('java -jar -Xmx5G dist/GNUSMailGoogle.jar -z -e')


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

def evaluateMOA(author,numWords,selector, alg):
	global graficas
	wordsFile = 'dataset' + selector + author + str(numWords) + '.list'
	order = """
cp %s ~/.gnusmail/wordlist.data
	""" % (wordsFile)
	os.system(order)
	createDataSet(author)
	print('java -javaagent:lib/sizeofag.jar -jar -Xmx5G dist/GNUSMailGoogle.jar -z -m --moa-classifier="%s"' % (alg)) #moaClassifier
	os.system('java -javaagent:lib/sizeofag.jar -jar -Xmx5G dist/GNUSMailGoogle.jar -z -m --moa-classifier="%s"' % (alg)) #moaClassifier
	newTasas = 'tasas' + author + alg.replace(" ","").replace("(","").replace(")","")
	order = """
mv tases %s
	""" % (newTasas)
	graficas.append(newTasas)
	os.system(order)

def getAuthors():
	return ['kitchen-l', 'lokay-m','sanders-r','beck-s','williams-w3', 'farmer-d', 'kaminski-v']

def getMoaAlgorithms():
	return ["MajorityClass", "HoeffdingTreeNBAdaptive","SingleClassifierDrift -d DDM -l HoeffdingTreeNBAdaptive", "SingleClassifierDrift -d EDDM -l HoeffdingTreeNBAdaptive", "OzaBagAdwin -l HoeffdingTreeNBAdaptive -s 10","SingleClassifierDrift -d DDM -l (OzaBag -l HoeffdingTreeNBAdaptive)"]

def createWordlistsForEveryAuthor():
	for author in getAuthors():
		print('---------------')
		print(author)
		print('---------------')
		createWordlists(author)

def imprimirgraficas(graficas, autor):
	filePng = "grafica_" + autor + ".png"
	sentencia = "plot "
	for gr in graficas:
		sentencia += '"' + gr + '" w l' 
		if gr <> graficas[-1]:
			sentencia += ', '
	print sentencia
	sentgnuplot = "echo 'set terminal png; set output \"" + filePng +  "\"; " + sentencia +  "' | gnuplot"
	os.system(sentgnuplot)

"""""""""
MAIN SECTION
"""""""""


#createWordlistsForEveryAuthor()
for author in getAuthors():
	for numWords in ['200','300','400'][:1]:
		for method in getAvailableSelectors():
			for alg in getMoaAlgorithms():
				evaluateMOA(author,numWords,method, alg)
	
	imprimirgraficas(graficas, author)
	graficas = []
