import csv

postal_codes = []

with open('us_postal_codes.csv', 'rb') as csvfile:
	codereader = csv.reader(csvfile, delimiter=',')
	for row in codereader:
		if row[0] != '':
			print row[0]
			postal_codes += [row[0]]
			
with open('postal_codes.csv', 'wb') as csvfile:
	codewriter = csv.writer(csvfile, delimiter=',')
	for code in postal_codes:
		codewriter.writerow([code])