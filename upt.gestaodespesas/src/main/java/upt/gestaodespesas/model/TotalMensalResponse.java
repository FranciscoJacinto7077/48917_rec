package upt.gestaodespesas.model;

public class TotalMensalResponse {
    private int ano;
    private int mes;
    private double total;

    public TotalMensalResponse() {}

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
