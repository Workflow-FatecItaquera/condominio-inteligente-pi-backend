import java.util.List;

/**
 * Versão Java equivalente do método aplicarRegras (ControleAcessoService.kt) para a ferramenta
 * ComplexGraph, que só lê Java. Mantém cada if e cada return do original; chamadas a repositórios
 * e a métodos de domínio viram métodos simples.
 */
public class AplicarRegras {

    public String aplicarRegras(Object visitanteId, long unidadeId, String documento) {
        Object visitante = localizarVisitante(visitanteId, documento);
        if (visitante == null) {
            return "CADASTRO_NAO_ENCONTRADO";
        }
        if (!visitanteAtivo(visitante)) {
            return "CADASTRO_INATIVO";
        }
        if (documentoDiverge(visitante, documento)) {
            return "DOCUMENTO_DIVERGENTE";
        }
        Object lista = buscarLista(unidadeId);
        if (lista == null) {
            return "FORA_DA_LISTA";
        }
        if (!listaVigente(lista)) {
            return "LISTA_VENCIDA";
        }
        if (!listaContem(lista, visitante)) {
            return "FORA_DA_LISTA";
        }
        List<Object> vinculadas = buscarAutorizacoes(visitante, unidadeId);
        if (vinculadas.isEmpty()) {
            if (temAutorizacaoEmOutraUnidade(visitante)) {
                return "AUTORIZACAO_OUTRA_UNIDADE";
            }
            if (temAutorizacaoDeOutraPessoa(unidadeId)) {
                return "AUTORIZACAO_OUTRA_PESSOA";
            }
            return "SEM_AUTORIZACAO";
        }
        Object escolhida = selecionarAutorizacao(vinculadas);
        if (escolhida == null) {
            return "AUTORIZACAO_CANCELADA";
        }
        if (dataAntesDoInicio(escolhida)) {
            return "ANTES_DO_PERIODO";
        }
        if (dataAposOFim(escolhida)) {
            return "APOS_O_PERIODO";
        }
        if (!dentroDoHorario(escolhida)) {
            return "FORA_DO_HORARIO";
        }
        if (ehDiaEspecial()) {
            if (!diaEspecialAutorizado(escolhida)) {
                return "FIM_DE_SEMANA_OU_FERIADO_SEM_PERMISSAO";
            }
        }
        return "PERMITIDA";
    }

    private Object localizarVisitante(Object id, String documento) { return null; }
    private boolean visitanteAtivo(Object v) { return true; }
    private boolean documentoDiverge(Object v, String documento) { return false; }
    private Object buscarLista(long unidadeId) { return null; }
    private boolean listaVigente(Object lista) { return true; }
    private boolean listaContem(Object lista, Object v) { return true; }
    private List<Object> buscarAutorizacoes(Object v, long unidadeId) { return null; }
    private boolean temAutorizacaoEmOutraUnidade(Object v) { return false; }
    private boolean temAutorizacaoDeOutraPessoa(long unidadeId) { return false; }
    private Object selecionarAutorizacao(List<Object> candidatas) { return null; }
    private boolean dataAntesDoInicio(Object a) { return false; }
    private boolean dataAposOFim(Object a) { return false; }
    private boolean dentroDoHorario(Object a) { return true; }
    private boolean ehDiaEspecial() { return false; }
    private boolean diaEspecialAutorizado(Object a) { return true; }
}
