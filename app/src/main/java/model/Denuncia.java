package model;

public class Denuncia {
    private String id, usuarioId, tipoDenuncia, localizacao, descricao, imageUrl;
    private String  status,responsavel;
    private String data;

    public Denuncia() {} // Necessário para Firebase

    public Denuncia(String id, String usuarioId, String tipoDenuncia, String localizacao, String descricao, String imageUrl, String data, String status, String responsavel) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tipoDenuncia = tipoDenuncia;
        this.localizacao = localizacao;
        this.descricao = descricao;
        this.imageUrl = imageUrl;
        this.data = data;
        this.status = status;
        this.responsavel = responsavel;
    }

    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public String getTipoDenuncia() { return tipoDenuncia; }
    public String getLocalizacao() { return localizacao; }
    public String getDescricao() { return descricao; }
    public String getImageUrl() { return imageUrl; }
    public String getData() { return data; }
    public String getStatus() { return status; }
    public String getResponsavel(){return responsavel;}

    public void setId(String id) {
        this.id = id;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }



    public String gettitulo(){
            String tipo = getTipoDenuncia();

            switch (tipo) {
                case "Energia":
                    return "Queda de energia";
                case "Água":
                    return "Falta de água";
                case "Acidente":
                    return "Novo acidente";
                case "Estrada":
                    return "problemas na estrada";
                default:
                    return "Tipo desconhecido";
            }
        }

    }


