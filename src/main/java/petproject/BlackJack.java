package petproject;

import org.w3c.dom.ls.LSOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class BlackJack {

    private  class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        @Override
        public String toString() {
            return value + '-' + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) {
                if (value == "A") {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value);
        }


        public boolean isAce(){
            return value == "A";

        }

        public String getImagePath(){
            return "/cards/" + toString() + ".png";
        }


    }

    ArrayList<Card> deck;
    Random random = new Random(); //for shuffling

    boolean isRestarted = false;


    //for dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    //for player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    //window
    int boardWidth = 600;
    int boardHeight = boardWidth;

    int cardWidth = 110;  //ratio 1 to 1.4
    int cardHeight = 154;

    JFrame frame = new JFrame("Black Jack by R");
    JPanel gamePanel = new JPanel(){
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            try{
                //hidden card
                Image hiddenCardImage = new ImageIcon(Objects.requireNonNull(getClass().
                        getResource("/cards/BACK.png"))).getImage();
                if(!stayButton.isEnabled()){
                    hiddenCardImage = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImage, 20, 20, cardWidth, cardHeight, null);

                //dealer card
                for (int i=0; i < dealerHand.size(); i++){
                    Card card = dealerHand.get(i);
                    Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImage, cardWidth + 27 + (cardWidth + 7)*i, 20, cardWidth, cardHeight, null);
                }

                //player card
                for (int i=0; i < playerHand.size(); i++){
                    Card card = playerHand.get(i);
                    Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImage, 27 + (cardWidth + 7)*i, 320, cardWidth, cardHeight, null);
                }

                if (isRestarted) {
                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.WHITE);
                    g.drawString("New Game !", 220, 250);
                    isRestarted = false;
                } else if (!stayButton.isEnabled()){
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();
                    System.out.println("STAY :");
                    System.out.println(dealerSum);
                    System.out.println(playerSum);

                    String message = "";
                    if (playerSum > 21){
                        message = "You lose!";
                    }
                    else if (dealerSum > 21){
                        message = "You win!";
                    }
                    else if (playerSum == dealerSum){
                        message = "Tie!";
                    }
                    else if (dealerSum > playerSum){
                        message = "You lose!";
                    }
                    else if (playerSum > dealerSum){
                        message = "You win!";
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.WHITE);
                    g.drawString(message, 220, 250);

                }

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");

    JButton restartButton = new JButton("Restart");


    BlackJack(){
        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(50,125,70));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        buttonPanel.add(restartButton);


        hitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Card card = deck.remove(deck.size()-1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                if (reducePlayerAce() > 21){
                    hitButton.setEnabled(false);
                }
                gamePanel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                hitButton.setEnabled(false);
                stayButton.setEnabled(false);
                while(dealerSum < 17){
                    Card card = deck.remove(deck.size()-1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }
                gamePanel.repaint();
            }
        });

        restartButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                resetGame();
                isRestarted = true;
                gamePanel.repaint();
                System.out.println("New game!");
            }
        });

        gamePanel.repaint();

    }

    private void resetGame() {

            deck = new ArrayList<>();
            String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
            String[] types = {"H", "D", "C", "S"};
            for (String value : values) {
                for (String type : types) {
                    deck.add(new Card(value, type));
                }
            }
            for (int i = 0; i < deck.size(); i++) {
                int swapIndex = random.nextInt(deck.size());
                Card temp = deck.get(i);
                deck.set(i, deck.get(swapIndex));
                deck.set(swapIndex, temp);
            }

            dealerHand = new ArrayList<>();
            playerHand = new ArrayList<>();
            dealerSum = 0;
            dealerAceCount = 0;
            playerSum = 0;
            playerAceCount = 0;

            hiddenCard = deck.remove(deck.size() - 1);
            dealerSum += hiddenCard.getValue();
            dealerAceCount += hiddenCard.isAce() ? 1 : 0;

            dealerHand.add(deck.remove(deck.size() - 1));
            dealerSum += dealerHand.get(0).getValue();
            dealerAceCount += dealerHand.get(0).isAce() ? 1 : 0;

            playerHand.add(deck.remove(deck.size() - 1));
            playerSum += playerHand.get(0).getValue();
            playerAceCount += playerHand.get(0).isAce() ? 1 : 0;

            playerHand.add(deck.remove(deck.size() - 1));
            playerSum += playerHand.get(1).getValue();
            playerAceCount += playerHand.get(1).isAce() ? 1 : 0;

            hitButton.setEnabled(true);
            stayButton.setEnabled(true);

            gamePanel.repaint();
        }

    public void startGame(){

        //for deck
        buildDeck();
        shuffleDeck();

        //for dealer
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size()-1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size()-1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("Dealer hand: ");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);

        //for player
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size()-1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        System.out.println("Player hand: ");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);
    }

    private void shuffleDeck() {

        for (int i = deck.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Card currenctCard = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, currenctCard);
        }

        System.out.println("After shuffling cards");
        System.out.println(deck);
    }

    public void buildDeck(){
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for(int i = 0; i < types.length; i++){
            for (int j = 0; j < values.length; j++){
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }
        System.out.println("Build Deck: ");
        System.out.println(deck);
    }

    private int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0){
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    private int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0){
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }
}
