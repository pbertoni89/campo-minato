package mf;

import java.util.Random;
import java.util.Scanner;


public class Gioco {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	static Scanner input = new Scanner(System.in);

	/**
	 * Era "?" ma anche "." rende bene
	 */
	final private String cover = ".";

	int nCelle;

	/**
	 * Array (nCelle x nCelle) di cio' che viene presentato all'utente (cioè il rendering)
	 * Qua NON SO ANCORA quante celle avrò, quindi semplicemente dico che ESISTE una variabile
	 * che si chiama caselle e che è di tipo array di array di stringhe
	 *
	 * Discorso ANALOGO per i due array delle mine
	 */
	String[][] caselle;

	int lvDifficolta;
	int nMine;
	int[] mineX;
	int[] mineY;

	long start, end;

	/**
	 * All'inizio non so ancora se ho vinto
	 */
	private boolean hoVinto = false;

	/**
	 * Costruttore: inizializziamo l'array
	 * @param lvDifficolta - il livello di difficoltà che questo gioco avrà
	 */
	public Gioco(int lvDifficolta) {

		switch (lvDifficolta)
		{
			case 1:
				nMine = 5;
				nCelle = 5;
				break;
			case 2:
				nMine = 10;
				nCelle = 10;
				break;
			case 3:
				nMine = 15;
				nCelle = 15;
				break;
			default:
				// capito qua se la difficoltà non è nè 1 nè 2 nè 3
				// TODO tira eccezione o scegli una difficoltà default... boh
				break;
		}

		// solo adesso so quante mine e quante celle ho
		this.caselle = new String[nCelle+1][nCelle+1];
		mineX = new int[nMine];      // se lo trovo ambiguo, utilizzo this.
		// this allunga leggermente quanto scrivo, ma di certo AIUTA
		this.mineY = new int[nMine];

		// inizializzo 1: la vista grafica
		start = System.currentTimeMillis();

		for (int i = 0; i < nCelle; i++) {
			for (int j = 0; j < nCelle; j++) {
				caselle[i][j] = cover;
			}
		}
		// inizializzo 2: lo stato delle mine
		assegnaMine();
	}


	public void stampaGriglia() {

		// stampa coordinate X
		System.out.println("\n" + ANSI_RED + "- - - - - - - - -" + ANSI_RESET + "\n");
		System.out.print("  ");
		for (int i = 0; i < nCelle; i++) {
			System.out.print(ANSI_RED + (i+1-1) + " " + ANSI_RESET);  // FIXME i+1
		}
		System.out.println(ANSI_RED + "Y" + ANSI_RESET);
		// stampa coordinate Y
		for (int i = 0; i < nCelle; i++) {
			System.out.print(ANSI_RED + (i+1-1) + " " + ANSI_RESET);  // FIXME i+1
			for (int j = 0; j < nCelle; j++) {
				System.out.print(caselle[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println(ANSI_RED + "X" + ANSI_RESET);

		System.out.flush();  // mi assicuro che venga scritta tutta
	}


	private void chiediCasella() throws Sconfitta {
		// TODO commentale!
						System.out.print("Mine:");
						for (int m = 0; m < nMine; m++)
							System.out.print("    " + mineX[m] + ", " + mineY[m]);
						System.out.println();
		// sottraggo già 1 poichè mi porto da numerazione utente a numerazione informatica
		// io so che i miei indici partono da 0, ma l'utente vuole vedere gli assi partire da 1
		System.out.println("\nInserisci la coordinata x: ");
		int x = input.nextInt(); // - 1;  // FIXME -1
		System.out.println("Inserisci la coordinata y: ");
		int y = input.nextInt(); // - 1;  // FIXME -1
		input.nextLine();

		// buona norma dichiarare final tutto ciò che viene assegnato una volta e NON cambia poi
		final boolean casellaValid = (x < nCelle && y < nCelle) && (x >= 0 && y >= 0);

		if (casellaValid)
		{
			modificaCasella(x, y);
		}
		else 
		{
			System.err.println("Errore, hai inserito delle coordinate errate");
			chiediCasella();
		}
	}


	private int intornoInf(int sel)
	{
		if (sel == 0)
			return sel;
		else
			return sel - 1;
	}


	private int intornoSup(int sel)
	{
		if (sel == nCelle - 1)
			return sel;
		else
			return sel + 1;
	}


	/**
	 * Conta le bombe adiacenti, dopodichè modifica la vista del campo.
	 * Infine, stampa la vista aggiornata
	 * @param x
	 * @param y
	 * @throws Sconfitta propaga l'eventuale sconfitta
	 */
	private void modificaCasella(int sel_x, int sel_y) throws Sconfitta
	{
		final int bombeAdiacenti = contaBombeAdiacenti(sel_x, sel_y);
		caselle[sel_x][sel_y] = String.valueOf(bombeAdiacenti);
		System.out.println("cella " + sel_x + ", " + sel_y + " ha " + bombeAdiacenti + " bombe adj");
		if (bombeAdiacenti == 0)
		{
			for (int x = intornoInf(sel_x); x <= intornoSup(sel_x); x++)
			{
				for (int y = intornoInf(sel_y); y <= intornoSup(sel_y); y++)
				{
					// a differenza dell'altro utilizzo dell'intorno, qua ho una ricorsione
					// e dunque se richiamo modificaCasella sulla stessa casella, è il cane che si morde la coda
					if (x == sel_x || y == sel_y)  // escludo le diagonali
					{
						if (x == sel_x && y == sel_y)  // escludo me stesso oppure ricorsione infinita
						{
							caselle[x][y] = "0";  // ho 0 adiacenti, sono io, bene è 0 
						}
						else
						{
							if (caselle[x][y] == cover)  // se l'ho già scoperta, cosa ci entro a fare
							{
								modificaCasella(x, y);
							}
						}
					}
				}
			}
		}
	}


	/**
	 * Volendo anche esso rientra nel costrutture - è la seconda parte dell'inizializzazione
	 * i costruttori sono fatti apposta per contenere tutto ciò che va fatto mentre un oggetto NASCE
	 */
	private void assegnaMine()
	{
		Random rand = new Random();
		for (int m = 0; m < nMine; m++)
		{
			boolean doppione = false;
			do
			{
				mineX[m] = rand.nextInt(nCelle);
				mineY[m] = rand.nextInt(nCelle);
				// evitare le ripetizioni !!!!
				for (int j = 0; j < m; j++)
					if (mineX[j] == mineX[m] && mineY[j] == mineY[m])
						doppione = true;
			} while (doppione);
		}
	}


	/**
	 * @param sel_x coordinata X della selezione
	 * @param sel_y coordinata Y della selezione
	 * @throws Sconfitta se ho selezionato una casella con una mina
	 * @return il numero di bombe adiancenti ovvero nell'intorno di (x, y)
	 */
	private int contaBombeAdiacenti(int sel_x, int sel_y) throws Sconfitta {

		int numBombe = 0;

		for (int m = 0; m < nMine; m++) 
		{
			// scorro in tutto l'intorno, casella compresa. Così ho già un buon punto per controllare
			for (int x = intornoInf(sel_x); x <= intornoSup(sel_x); x++)
			{
				for (int y = intornoInf(sel_y); y <= intornoSup(sel_y); y++)
				{
					if (mineX[m] == x && mineY[m] == y)
					{
						if (x == sel_x && y == sel_y)
						{
							throw new Sconfitta();
						}
						numBombe++;
					}
				}
			}
		}

		return numBombe;
	}


	/**
	 * La condizione per la vittoria è semplice: tutte e sole le cover rimaste sono MINE
	 * @return vero se e solo se ho vinto
	 */
	private boolean controllaVittoria()
	{
		for (int x = 0; x < nCelle; x++)
		{
			for (int y = 0; y < nCelle; y++)
			{
				if (caselle[x][y].equals(cover))
				{
					boolean coverMina = false;
					// allora DEVE essere una mina. Sennò, a questo punto del gioco non ho vinto di sicuro
					for (int m = 0; m < nMine; m++)
					{
						if (mineX[m] == x && mineY[m] == y)
						{
							coverMina = true;
							break;  // se voglio ridurre i cicli
						}
					}

					if (! coverMina)
					{
						System.out.println("non vinco a " + x + ", " + y);
						return false;  // vuol dire che c'è ancora almeno una NON-mina ignota
					}
				}
			}
		}

		return true;
	}


	/**
	 * Risolve tutti i cover scoprendo cosa c'è sotto, Se è una mina, li sostituisce con #
	 */
	private void scopriGriglia()
	{
		for (int x = 0; x < nCelle; x++)
		{
			for (int y = 0; y < nCelle; y++)
			{
				if (caselle[x][y].equals(cover)) // solo le cover sono da scoprire
				{
					try {
						int bombeAdiacenti = contaBombeAdiacenti(x, y);
						caselle[x][y] = String.valueOf(bombeAdiacenti);
					} catch (Sconfitta e) {
						// ho già perso... che me ne frega
						caselle[x][y] = "#";
					}
				}
			}
		}
	}


	/**
	 * Dato un gioco inizializzato, inizia a giocare finchè non si è vinto o perso
	 */
	public void gioca() {

		try {
			// continuo a giocare. Prima il flusso del gioco era ricorsivo - ora iterativo
			while (hoVinto == false) {
				stampaGriglia();
				chiediCasella();
				hoVinto = controllaVittoria();
			}
		}
		catch (Sconfitta s)
		{
			System.err.println("sconfitta!!!");
			System.err.flush();  // sospende l'esecuzione finchè quel buffer non è stato scritto
			scopriGriglia();
			stampaGriglia();
			if (hoVinto == true) {
				System.err.println("NON HA SENSO");  // significa errore logico
			}
			hoVinto = false;  // risolvo l'eventuale incoerenza logica
		}

		if (hoVinto == true)
		{
			System.out.println(ANSI_GREEN + "Hai vinto!" + ANSI_RESET);
		}

		end = System.currentTimeMillis();
		System.out.println("La partita è durata: " + ((end - start) / 1000) + " secondi");

		// logicamente sbagliato chiamare exit. Il controllo ritorna al main()
		// System.exit(0);
	}
}

//   4, 1    0, 0    4, 3    4, 0    2, 2
