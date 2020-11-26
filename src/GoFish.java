import java.util.*;

public class GoFish {

    private final String[] SUITS = { "C", "D", "H", "S" };
    private final String[] RANKS = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K" };

    private char whoseTurn;
    private final Player player1;
    private final Player player2;
    private final Player computer;
    private List<Card> deck;
    private final Scanner in;
    private static boolean[] requestedThisTurn = {false, false, false, false, false, false, false, false, false, false, false, false, false};
    private String choice = null;

    public GoFish() {
        this.whoseTurn = 'P';
        this.player1 = new Player();
        this.player2 = new Player();
        this.computer = new Player();
        this.in = new Scanner(System.in);
    }

    public void askType() {
        do {
            System.out.println("Type in 'P' to play 2 player or type in 'C' to play against the computer: ");
            choice = in.nextLine().toUpperCase();
        } while (choice.equals("P") == false && choice.equals("C")== false);
        if (choice.equals("C")) {
            play();
        } else if (choice.equals("P")){
            multiplay();
        }
    }

    public void multiplay() {

        shuffleAndDeal2();

        while (true) {
            if (whoseTurn == 'P') {
                whoseTurn = takeTurn2(true);

                if (player1.findAndRemoveBooks()) {
                    System.out.println("PLAYER 1: Oh, that's a book!");
                    showBooks2(true);
                }
            } else if (whoseTurn == 'Q') {
                whoseTurn = takeTurn2(false);

                if (player2.findAndRemoveBooks()) {
                    System.out.println("PLAYER 2: Oh, that's a book!");
                    showBooks2(false);
                }
            }

            int player1Books = player1.getBooks().size();
            int player2Books = player2.getBooks().size();

            String win1Message = "Congratulations PLAYER 1, you won " + player1Books + " books to " + player2Books + ".";
            String win2Message = "Congratulations PLAYER 2, you won " + player2Books + " books to " + player1Books + ".";
            String tieMessage = "Looks like it's a tie, " + player1Books + " to " + player2Books + ".";

            if (player1Books + player2Books == 13) {
                if (player1.getBooks().size() > computer.getBooks().size()) {
                    System.out.println("\n" + win1Message);
                } else {
                    System.out.println("\n" + win2Message);
                }
                break;
            } else if (deck.size() == 0) {
                System.out.println("\nOh no, there are no more cards in the deck!");

                if (player1Books > player2Books) {
                    System.out.println(win1Message);
                } else if (player2Books > player1Books) {
                    System.out.println(win2Message);
                } else {
                    System.out.println(tieMessage);
                }
                break;
            }
        }

    }

    public void play() {
        shuffleAndDeal();

        // play the game until someone wins

        while (true) {
            if (whoseTurn == 'P') {
                whoseTurn = takeTurn(false);

                if (player1.findAndRemoveBooks()) {
                    System.out.println("PLAYER: Oh, that's a book!");
                    showBooks(false);
                }
            } else if (whoseTurn == 'C') {
                whoseTurn = takeTurn(true);

                if (computer.findAndRemoveBooks()) {
                    System.out.println("CPU: Oh, that's a book!");
                    showBooks(true);
                }
            }

            // the games doesn't end until all 13 books are completed, or there are
            // no more cards left in the deck. the player with the ,ost books at the
            // end of the game wins.

            int playerBooks = player1.getBooks().size();
            int computerBooks = computer.getBooks().size();

            String winMessage = "Congratulations, you win! " + playerBooks + " books to " + computerBooks + ".";
            String loseMessage = "Maybe next time. You lose " + computerBooks + " books to " + playerBooks + ".";
            String tieMessage = "Looks like it's a tie, " + playerBooks + " to " + computerBooks + ".";

            if (playerBooks + computerBooks == 13) {
                if (player1.getBooks().size() > computer.getBooks().size()) {
                    System.out.println("\n" + winMessage);
                } else {
                    System.out.println("\n" + loseMessage);
                }
                break;
            } else if (deck.size() == 0) {
                System.out.println("\nOh no, there are no more cards in the deck!");

                if (playerBooks > computerBooks) {
                    System.out.println(winMessage);
                } else if (computerBooks > playerBooks) {
                    System.out.println(loseMessage);
                } else {
                    System.out.println(tieMessage);
                }
                break;
            }
        }
    }

    public void shuffleAndDeal() {
        if (deck == null) {
            initializeDeck();
        }
        Collections.shuffle(deck);  // shuffles the deck

        while (player1.getHand().size() < 7) {
            player1.takeCard(deck.remove(0));    // deal 7 cards to the
            computer.takeCard(deck.remove(0));  // player and the computer
        }
    }

    public void shuffleAndDeal2() {
        if (deck == null) {
            initializeDeck();
        }
        Collections.shuffle(deck);

        while (player1.getHand().size() < 7) {
            player1.takeCard(deck.remove(0));
            player2.takeCard(deck.remove(0));
        }
    }

    public void updateCurrentTurnRequests(Card rank){
        requestedThisTurn[Card.getOrderedRank(rank.getRank()) - 2] = false;
    }

    public void resetCurrentTurnRequests(){
        for(int i = 0; i < requestedThisTurn.length; i++){
            requestedThisTurn[i] = true;
        }
    }

    public static boolean[] getRequestedThisTurn(){
        return requestedThisTurn;
    }

    ////////// PRIVATE METHODS /////////////////////////////////////////////////////

    private void initializeDeck() {
        deck = new ArrayList<>(52);

        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(new Card(rank, suit));     // adds 52 cards to the deck (13 ranks, 4 suits)
            }
        }
    }

    private char takeTurn(boolean cpu) {
        showHand(cpu);
        showBooks(cpu);

        // if requestCard returns null, then the hand was empty and new card was drawn.
        // this restarts the turn, ensuring the updated hand is printed to the console.

        Card card = requestCard(cpu);
        if (card == null) {
            return cpu ? 'C' : 'P';     // restart this turn with updated hand
        }

        // check if your opponent has the card you requested. it will be automatically
        // relinquished if you do. otherwise, draw from the deck. return the character
        // code for whose turn it should be next.

        if (!cpu) {
            if (computer.hasCard(card)) {
                System.out.println("CPU: Yup, here you go!");
                computer.relinquishCard(player1, card);

                return 'P';
            } else {
                System.out.println("CPU: Nope, go fish!");
                player1.takeCard(deck.remove(0));

                return 'C';
            }
        } else {
            if (player1.hasCard(card)) {
                System.out.println("CPU: Oh, you do? Well, hand it over!");
                player1.relinquishCard(computer, card);

                return 'C';
            } else {
                System.out.println("CPU: Ah, I guess I'll go fish...");
                computer.takeCard(deck.remove(0));


                return 'P';
            }
        }
    }

    private char takeTurn2(boolean player) {
        showHand2(player);
        showBooks2(player);

        // if requestCard returns null, then the hand was empty and new card was drawn.
        // this restarts the turn, ensuring the updated hand is printed to the console.

        Card card = requestCard2(player);
        if (card == null) {
            return player ? 'Q' : 'P';     // restart this turn with updated hand
        }

        // check if your opponent has the card you requested. it will be automatically
        // relinquished if you do. otherwise, draw from the deck. return the character
        // code for whose turn it should be next.

        if (player) {
            if (player2.hasCard(card)) {
                System.out.println("Player 2: Yup, here you go!");
                player2.relinquishCard(player1, card);

                return 'P';
            } else {
                System.out.println("Player 2: Nope, go fish!");
                player1.takeCard(deck.remove(0));

                return 'Q';
            }
        } else {
            if (player1.hasCard(card)) {
                System.out.println("Player 1: Yup, here you go!");
                player1.relinquishCard(player2, card);

                return 'Q';
            } else {
                System.out.println("Player 1: Nope, go fish!");
                player2.takeCard(deck.remove(0));


                return 'P';
            }
        }
    }

    private Card requestCard(boolean cpu) {
        Card card = null;

        // request a card from your opponent, ensuring that the request is valid.
        // if your hand is empty, we return null to signal the calling method to
        // restart the turn. otherwise, we return the requested card.

        while (card == null) {
            if (!cpu) {
                if (player1.getHand().size() == 0) {
                    player1.takeCard(deck.remove(0));

                    return null;
                } else {
                    System.out.print("PLAYER: Got any... ");
                    String rank = in.nextLine().trim().toUpperCase();
                    card = Card.getCardByRank(rank);
                }
            } else {
                if (computer.getHand().size() == 0) {
                    computer.takeCard(deck.remove(0));

                    return null;
                } else {
                    card = computer.getCardByNeed();
                    card = computer.getCardByNeed();
                    System.out.println("CPU: Got any... " + card.getRank());
                }
            }
        }

        return card;
    }

    private Card requestCard2(boolean player) {
        Card card = null;

        // request a card from your opponent, ensuring that the request is valid.
        // if your hand is empty, we return null to signal the calling method to
        // restart the turn. otherwise, we return the requested card.

        while (card == null) {
            if (player) {
                if (player1.getHand().size() == 0) {
                    player1.takeCard(deck.remove(0));

                    return null;
                } else {
                    System.out.print("PLAYER 1: Got any... ");
                    String rank = in.nextLine().trim().toUpperCase();
                    card = Card.getCardByRank(rank);
                }
            } else {
                if (player2.getHand().size() == 0) {
                    player2.takeCard(deck.remove(0));

                    return null;
                } else {
                    System.out.print("PLAYER 2: Got any... ");
                    String rank = in.nextLine().trim().toUpperCase();
                    card = Card.getCardByRank(rank);
                }
            }
        }

        return card;
    }

    private void showHand(boolean cpu) {
        if (!cpu) {
            System.out.println("\nPLAYER hand: " + player1.getHand());   // only show player's hand
        }
    }

    private void showHand2(boolean player) {
        if (player) {
            System.out.println("\nPLAYER 1 hand: " + player1.getHand());
        } else {
            System.out.println("\nPLAYER 2 hand: " + player2.getHand());
        }
    }

    private void showBooks(boolean cpu) {
        if (!cpu) {
            System.out.println("PLAYER books: " + player1.getBooks());   // shows the player's books
        } else {
            System.out.println("\nCPU books: " + computer.getBooks());  // shows the computer's books
        }
    }

    private void showBooks2(boolean player) {
        if (player) {
            System.out.println("PLAYER 1 books: " + player1.getBooks()); //shows player 1 books
        } else {
            System.out.println("PLAYER 2 books: " + player2.getBooks()); // shows player 2 books
        }
    }

    ////////// MAIN METHOD /////////////////////////////////////////////////////////

    public static void main(String[] args) {
        System.out.println("#########################################################");
        System.out.println("#                                                       #");
        System.out.println("#   ####### #######   ####### ####### ####### #     #   #");
        System.out.println("#   #       #     #   #          #    #       #     #   #");
        System.out.println("#   #  #### #     #   #####      #    ####### #######   #");
        System.out.println("#   #     # #     #   #          #          # #     #   #");
        System.out.println("#   ####### #######   #       ####### ####### #     #   #");
        System.out.println("#                                                       #");
        System.out.println("#   A human v. CPU rendition of the classic card game   #");
        System.out.println("#   Go Fish. Play the game, read and modify the code,   #");
        System.out.println("#   and make it your own!                               #");
        System.out.println("#                                                       #");
        System.out.println("#########################################################");

        new GoFish().askType();
    }
}