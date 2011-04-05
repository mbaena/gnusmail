from rpy import *


def convertFileToVector(file):
    f = open(file)
    vector = map(lambda l : float(l), f.readlines())
    f.close()
    return vector


def getFiles():
    return ["beck.rates", "farmer.rates", "kaminski.rates", "kitchen.rates", "lokay.rates", "sanders.rates", "williams.rates"]

freqs = []
freqssinceros = []
for file in getFiles():
    rates = convertFileToVector(file)
    freqs.append(rates)
    rateslimpias = [rate for rate in rates if rate > 0]
    freqssinceros.append(rateslimpias)

r.pdf("boxplot.pdf")
r.boxplot(freqs)
r.pdf("boxplotnoceros.pdf")
r.boxplot(freqssinceros)
