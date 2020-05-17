public class Transaction {
    long id;
    String payee;
    String payer;
    double tx_amount;
    double fee;
    float worth;
    int tx_size;
    long date;

    public Transaction(long i, double tx, float w, long d) {
        this.id = i;
        this.payee = "John";
        this.payer = "Adam";
        this.tx_amount = tx;
        this.fee = 0.00000001;
        this.worth = w;
        this.tx_size = 500;
        this.date = d;
    };

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", payee='" + payee + '\'' +
                ", payer='" + payer + '\'' +
                ", tx_amount=" + tx_amount +
                ", fee=" + fee +
                ", worth=" + worth +
                ", tx_size=" + tx_size +
                ", date=" + date +
                '}';
    }

    public String getPayee() {
        return payee;
    }

    public String getPayer() {
        return payer;
    }

    public double getTx_amount() {
        return tx_amount;
    }

    public float getWorth() {
        return worth;
    }

    public int getTx_size() {
        return tx_size;
    }

    public long getDate() {
        return date;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public long getId() {
        return id;
    }
}
