#coding=utf-8
"""
- En el directorio donde se ejecute este script, debe haber un acceso a weka (un vínculo, p.e.)
- En ~.gnusmail debe haber un acceso, llamdo maildir, a la carpeta raiz del dataset de enron (ln -s)
"""

import os

def createLink(author):
	order = """
rm ~/.gnusmail/maildirln ; ln -s ~/.gnusmail/maildir/%s ~/.gnusmail/maildirln
	""" % (author)
	print(order)
	os.system(order)

def createFilteringString(selector, nwords,author):
        output = "dataset" + selector + author + str(nwords) + ".arff"
        return """
java -cp weka.jar weka.filters.supervised.attribute.AttributeSelection -E \"weka.attributeSelection.%s \" -S "weka.attributeSelection.Ranker -T -3.0E-4 -N %s" -c \"first" -i ~/.gnusmail/dataset.arff  %s
        """ % (selector, nwords, output), output

def executeOrder(order):
        os.system(order)

def getAvailableSelectors():
        return ['GainRatioAttributeEval', 'ChiSquaredAttributeEval', 'ReliefFAttributeEval', 'InfoGainAttributeEval', 'OneRAttributeEval']

def extractWordsFromArff(file):
        f = open(file)
        lines = [line for line in f.readlines() if line.startswith('@attribute')]
        words = [line.split()[1] for line in lines]
        f.close()
        wordsfile = file.replace('arff','list')
        f = open(wordsfile,"w")
        for word in words:
		if word[0].islower():
			f.write(word + '\n')
        f.close()


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

def evaluate(author,numWords,selector):
	wordsFile = 'dataset' + selector + str(numWords) + author + '.list'
	order = """
cp %s ~/.gnusmail/wordlist.data
	""" % (wordsFile)
	print(order)
	os.system(order)
	createLink(author)
	os.system('rm ~/.gnusmail/atributos.csv')
	os.system('rm ~/.gnusmail/dataset.arff')
	os.system('rm ~/.gnusmail/model.bin')
	os.system('java -jar -Xmx5G dist/GNUSMailGoogle.jar -z -b')
	os.system('java -jar -Xmx5G dist/GNUSMailGoogle.jar -z -e -i')
	newTasas = 'tasasWeka' + author + selector + str(numWords)
	order = """
mv tases %s
	""" % (newTasas)
	os.system(order)
	newDS = "dataset" + author + selector + str(numWords) + ".arff"
	os.system("mv ~/.gnusmail/dataset.arff " + newDS)

def getAuthors():
	return ['kitchen-l', 'lokay-m','sanders-r','beck-s','williams-w3', 'farmer-d', 'kaminski-v']

def createWordlistsForEveryAuthor():
	for author in getAuthors():
		print('---------------')
		print(author)
		print('---------------')
		createWordlists(author)

"""""""""
MAIN SECTION
"""""""""


#createWordlistsForEveryAuthor()
for author in getAuthors():
	for numWords in ['200','300','400']:
		for method in getAvailableSelectors():
			evaluate(author,numWords,method)
