Za instalaciju i pokretanje sustava potrebno je napraviti slijedeće:
	1.	WASMPOTE
		- Putem XCTU-a podesiti i konfigurirati XBee na Waspmote Gatewayu prema uputama: http://www.libelium.com/development/waspmote/documentation/x-ctu-tutorial/
			- panID 3332
			- kanal C
			- Bitno je da su XBee na Waspmotu i na Gatewayu jednako konfigurirani. U slučaju poteškoća konfigurirati XBee na Waspmoteu putem XCTU-a

		- Na Waspmote uređaj potrebno je korištenjem Waspmote IDE-a staviti kod koji se nalazi u datoteci hall.pde. (SOFTWARE/Programski kod/Waspmote/hall.pde)
			- u kodu je najprije potrebno izmijeniti RX_ADDRESS tako da MAC adresa odgovara MAC adresi Waspmote Gateway-a (Primatelja).
		- Senzor Hallovog efekta potrebno je priključiti na socket broj 2 Sensor Boarda.
		- Priključenjem na napajanje Waspmote je podešen i funkcionalan.
		- Zanemariti dojavu grešakan na poslužitelju, pri prvoj promjeni stanja senzora poslužitelj kreće ispisivati poruke.

	2.	Poslužitelj
		- Poslužitelj se može pokrenuti putem izvršne datoteke ili preko Eclipsea. 
		a. Za Eclipse dovoljno je import projekta u direktorju Server. Kao argument se predaje ime serial porta na koji je prikljućen gateway. 
		Npr. za macbook: /dev/tty.usbserial-AH019V3Y

		b. Pokretanje putem jar-a:
		java -Djava.library.path=/Users/lbunicic/Documents/Faks/Završni\ rad/Server/zavrsni/libs -jar posluzitelj.jar /dev/tty.usbserial-AH019V3Y

			- path označuje put do direktorija u kojem se nalazi librxtxSerial.jnilib(za Mac) i moguće je izostaviti tu naredbu ako se nalaze u istom direktoriju
			- posluzitelj.jar je izvršna datoteka
			- /dev/tty.usbserial-AH019V3Y je primjer usb vrata na kojima je gateway


	3.	Android aplikacija

		- Moguće pokrenuti importom programskog koda u Android studio te pokretanje unutar Android Studioa putem USB kabela ili preuzimanjem i pokretanjem .apk datoteke na Android uređaj.