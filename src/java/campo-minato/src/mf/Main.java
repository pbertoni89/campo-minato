package mf;

/**
 * Descrivere da qualche parte in documentazione le regole del gioco. Mi è molto comodo
 * averle sempre sott'occhio mentre programmo !!!
 *
 * Devo ripulire dalle '0' contigue
 * Vinco quando tutte e sole le mine sono '?'
 */

public class Main {

	public static void main(String[] args) {

		// si chiamano come quelle dentro al gioco, ma quelle sono variabili ATTRIBUTO,
		// queste no. Queste sono variabili d'appoggio dove salvo il valore scaricato
		// da tastiera
		int selezioneMain = 0;
		int lvDifficolta;

		do {
			System.out.println("Benvenuto! Questo e' Campo Minato\n"
					+ "Scrivi da tastiera un numero di quelli indicati sotto\n"
					+ "Buon divertimento\n"
					+ "0) esci\n"
					+ "1) play\n"
					+ "2) credits\n");

			System.out.print("Inserisci il valore: ");

			selezioneMain = Gioco.input.nextInt();
			Gioco.input.nextLine();
			// FIXME boh capire perchè si schianta. logicamente è corretto ma forse è al momento sbagliato scanner.close();
			// scanner.close();

			switch (selezioneMain) {
				case 1:
				{
					System.out.println("Seleziona il livello di difficoltà\n"
							+ "1 Facile | 2 Medio | 3 Difficile\n ");

					lvDifficolta = Gioco.input.nextInt();

					// qua costruisco UN nuovo gioco. E dunque gli PASSO tutto e solo
					// quello che gli serve sapere per cominciare !
					Gioco gioco = new Gioco(lvDifficolta);
					gioco.gioca();
				}
				case 2:
				{
				System.out.println("questo gioco e' stato programmato da \n"
						+ "Busetti Giorgio\n"
						+ "Franini Manuel\n"
						+ " ");
				}
			}
		}
		while (selezioneMain != 0);

		// che mi serve qua System.out.println("inserisci un numero da 1 a 2");
		Gioco.input.close();

		System.out.println("ciao ciao");
	}
}