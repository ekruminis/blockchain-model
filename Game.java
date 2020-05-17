import java.io.File;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

public class Game {
    LinkedList<Transaction> mempool = new LinkedList<>();

    double tx_amount;
    double fee;
    float worth;
    long date;
    float importance;
    double net_state;
    double min_fee = 0.00000001;
    double fee_probability = 3895410;
    double exchange_rate = 0.00006196;
    long block_size = 8400;
    long mempool_size = mempool.size();
    int block_interval = 600;
    double patience;

    long globalDate = 0; // append this each block cycle
    long id = 1;
    LinkedList<Transaction> losers = new LinkedList<>();

    public void genTransaction(int number) {
        while(number > 0) {
            double x = genAmount();
            float w = calcWorth(x);
            long d = globalDate;
            Transaction tx = new Transaction(id, x, w, d);
            mempool.add(tx);
            id++;
            number--;
        }
    }

    public double genAmount() {
        return Math.abs(new Random().nextGaussian() * 4 + 4);
    }

    public double calcFee(Transaction x) {
        double f = x.getFee();

        if(Math.pow(net_state/min_fee, x.getWorth()) >= fee_probability) {
            f = min_fee;
        }

        return f;
    }

    public float calcWorth(double tx) {
        float w = 0;
        double v = new Random().nextDouble();

        w = (float) (v / (v + (Math.pow(Math.exp(1), -(tx * v)))));
        return w;
    }

    public float calcUtility(Transaction x) {
        // (1-w)^(curD-dtx/interval*2e)
        return (float) Math.pow( (1-x.getWorth()), ((globalDate-x.getDate())/(block_interval*(Math.exp(1)*2))) );
    }

    public LinkedList<Transaction> getmempool() {
        return mempool;
    }

    public double calcNetworkState() {
        mempool_size = mempool.size();
        net_state = (mempool_size/block_size/exchange_rate);
        return net_state;
    }

    public void play(int sets, int incomingTx) {
        LinkedList totalFees = new LinkedList();
        LinkedList totalUtility = new LinkedList();
        LinkedList totalMempool = new LinkedList();

        int plays = sets;

        while (plays != 0) {
            System.out.println("GAME STARTING: " + (globalDate / 600));
            genTransaction(incomingTx);

            LinkedList<Transaction> players = (LinkedList<Transaction>) mempool.clone();
            LinkedList<Transaction> winners = new LinkedList<>();

            double nt = calcNetworkState();

            while (losers.size() != 0 && winners.size() < block_size) {
                winners.addFirst(losers.getLast());
                min_fee = losers.getLast().getFee();
                players.remove(losers.getLast());
                losers.removeLast();
            }

            losers.clear();

            int i = 0;
            while (players.size() != 0) {
                Transaction p = players.get(0);

                if (winners.size() >= block_size && winners.getFirst().getFee() == winners.getLast().getFee()) {
                    min_fee += 0.00000001;
                }

                double f = calcFee(p);

                if (winners.size() < block_size) {
                    players.remove(p);
                    p.setFee(f);
                    winners.addFirst(p);
                } else if (f >= winners.getFirst().getFee() && winners.size() >= block_size) {
                    players.remove(p);
                    p.setFee(f);
                    players.add(winners.getLast());
                    winners.removeLast();
                    winners.addFirst(p);
                } else if (f == p.getFee()) {
                    players.remove(p);
                    losers.add(p);
                }

//            System.out.println("net state: " + nt);
//            System.out.println(p.toString());
//            System.out.println("min fee: " + min_fee);
//            System.out.println("full: " + (winners.size() >= block_size));
//            System.out.println("2nd: " + (winners.getFirst().getFee() == min_fee));
//            System.out.println("winners : " + winners.toString());

            }

            //System.out.println("_________");
            //System.out.println("-WINNERS-");
            //winners.forEach(t -> System.out.println(t.toString()));
            //System.out.println(winners.getFirst().toString());
            //System.out.println(winners.getLast().toString());

            //System.out.println("__________");
            //System.out.println("--LOSERS--");
            //losers.forEach(t -> System.out.println(t.toString()));
            //System.out.println(losers.getLast());

            for (Transaction tx : winners) {
                mempool.removeIf(t -> t.getId() == tx.getId());
            }

            float avgUtility = 0;
            double avgFee = 0;

            for (Transaction t : winners) {
                avgUtility += calcUtility(t);
                avgFee += t.getFee();
            }


            globalDate += 600;

            System.out.println("mempool size:   " + mempool.size());
            System.out.println("avg utility:    " + (avgUtility / block_size));
            System.out.println("avg fee:        " + (avgFee / block_size));

            totalFees.add(avgFee / block_size);
            totalUtility.add(avgUtility / block_size);
            totalMempool.add(mempool.size());
            plays--;
        }

        try {
            File myObj = new File("results.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }

            FileWriter myWriter = new FileWriter("results.txt");

            myWriter.write("avg utility:    ");
            myWriter.write(System.lineSeparator());
            totalUtility.forEach((u) -> {
                try {
                    myWriter.write(String.format("%.12f", u));
                    myWriter.write(System.lineSeparator());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            myWriter.write("avg fees:       ");
            myWriter.write(System.lineSeparator());
            totalFees.forEach((f) -> {
                try {
                    myWriter.write(String.format("%.12f", f));
                    myWriter.write(System.lineSeparator());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            myWriter.write("avg mempool:    ");
            myWriter.write(System.lineSeparator());
            totalMempool.forEach((m) -> {
                try {
                    myWriter.write(m.toString());
                    myWriter.write(System.lineSeparator());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            myWriter.close();
        }
        catch(IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
