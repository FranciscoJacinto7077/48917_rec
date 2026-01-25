package upt.gestaodespesas.mapper;

import upt.gestaodespesas.dto.UserMeResponse;
import upt.gestaodespesas.entity.Utilizador;

public final class UtilizadorMapper {

    private UtilizadorMapper() {}

    public static UserMeResponse toMeResponse(Utilizador u) {
        if (u == null) return null;
        return new UserMeResponse(u.getId(), u.getNome(), u.getEmail());
    }
}
