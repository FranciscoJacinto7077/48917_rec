package upt.gestaodespesas.dto;

import upt.gestaodespesas.entity.Categoria;
import upt.gestaodespesas.entity.Despesa;
import upt.gestaodespesas.entity.Utilizador;

public final class DtoMapper {
    private DtoMapper() {}

    public static UserMeResponse toUserMe(Utilizador u) {
        if (u == null) return null;
        return new UserMeResponse(u.getId(), u.getNome(), u.getEmail());
    }

    public static CategoriaResponse toCategoriaResponse(Categoria c) {
        if (c == null) return null;
        return new CategoriaResponse(c.getId(), c.getNome());
    }

    public static DespesaResponse toDespesaResponse(Despesa d) {
        if (d == null) return null;
        return new DespesaResponse(
                d.getId(),
                d.getData(),
                d.getDescricao(),
                d.getValor(),
                d.getMetodoPagamento(),
                toCategoriaResponse(d.getCategoria())
        );
    }
}