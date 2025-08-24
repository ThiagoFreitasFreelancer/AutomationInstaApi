package com.automacao.instagram;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import java.util.HashMap;
import java.util.Random;

public class InstagramPublisher {

    public static int random(int min, int max) {

        Random num = new Random();
        int result = num.nextInt((max - min)) + min;
        // Casting int for string
        return result;
    }
    
    // URL da imagem a ser publicada. Deve ser uma URL pública.

    public static String [] imagens = {
            "zPLKXRp",
            "XiGcVcJ",
            "MBIRsOP",
            "56fUZcW",
            "OCZswru",
            "AoxRDZE",
            "Av0qWUU"
    };

    private static final String[] textLegend = {
            "Acesse nosso site: punkcodesolution.com.br",
            "Transformando Ideias em Código. Pronto para Construir o Futuro Conosco? – Visite punkcodesolution.com.br",
            "Soluções Inovadoras para um Mundo Digital – Visite punkcodesolution.com.br",
            "Vamos Criar a Próxima Grande Coisa. Junte-se a Nós em punkcodesolution.com.br",
            "Onde Tecnologia de Ponta Encontra Mentes Criativas. Descubra Mais em punkcodesolution.com.br"
    };

    public static String IMAGE_URL = "https://i.imgur.com/" + imagens[(int) random(0, imagens.length - 1)] + ".png";
    public static String CAPTION = textLegend[(int) random(0, textLegend.length - 1)];

    public static void main(String[] args) {
        try {
            // Passo 1: Criar o contêiner de mídia
            String containerId = createMediaContainer();

            if (containerId != null) {
                System.out.println("Contêiner de mídia criado com sucesso! ID: " + containerId);

                // É recomendável adicionar um pequeno atraso para garantir que a mídia esteja pronta
                // Thread.sleep(5000); // 5 segundos de espera (opcional, mas recomendado)
                
                // Passo 2: Publicar a mídia a partir do contêiner
                publishMedia(containerId);
            }
        } catch (Exception e) {
            System.err.println("Ocorreu um erro durante a publicação: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                // Fechar a conexão Unirest
                Unirest.shutDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<String, Object> getJson() {

        HashMap<String, Object> model = new HashMap<>();
        model.put("domain","INSTAGRAM");

        return model;
    }

    public static String createMediaContainer() throws Exception {
        System.out.println("Iniciando a criação do contêiner de mídia...");

        String url = "https://graph.instagram.com/v23.0/" + System.getenv("INSTAGRAM_ACCOUNT_ID") + "/media";

        HttpResponse<JsonNode> response = Unirest.post(url)
                .queryString("image_url", IMAGE_URL)
                .queryString("caption", CAPTION)
                .queryString("access_token", System.getenv("ACCESS_TOKEN")) // Corrigido o erro de digitação
                .asJson();

        if (response.getStatus() == 200) {
            JSONObject jsonResponse = response.getBody().getObject();
            return jsonResponse.getString("id");
        } else {
            System.err.println("Erro ao criar o contêiner. Status: " + response.getStatus());
            System.err.println("Corpo da resposta: " + response.getBody().toString());
            return null;
        }
    }

    public static void publishMedia(String containerId) throws Exception {
        System.out.println("Iniciando a publicação da mídia...");

        String url = String.format("https://graph.instagram.com/v23.0/%s/media_publish", System.getenv("INSTAGRAM_ACCOUNT_ID"));

        HttpResponse<JsonNode> response = Unirest.post(url)
                .queryString("creation_id", containerId)
                .queryString("access_token", System.getenv("ACCESS_TOKEN"))
                .asJson();

        if (response.getStatus() == 200) {
            JSONObject jsonResponse = response.getBody().getObject();
            System.out.println("Mídia publicada com sucesso! ID: " + jsonResponse.getString("id"));
        } else {
            System.err.println("Erro ao publicar a mídia. Status: " + response.getStatus());
            System.err.println("Corpo da resposta: " + response.getBody().toString());
        }
    }
}