package upt.gestaodespesas.dto.analytics;

public class TotalMensalResponse {
    private int ano;
    private int mes;
    private double total;

    public TotalMensalResponse() {}

    public TotalMensalResponse(int ano, int mes, double total) {
        this.ano = ano;
        this.mes = mes;
        this.total = total;
    }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
