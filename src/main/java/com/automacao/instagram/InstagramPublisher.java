package com.automacao.instagram;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

public class InstagramPublisher {

    public static String gerarImagemComOpenAI(String prompt) throws Exception {

        String apiKey = System.getenv("API_KEY"); // Coloque sua chave da OpenAI aqui
        URL url = new URL("https://api.openai.com/v1/images/generations");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject body = new JSONObject();
        body.put("model", "dall-e-2");
        body.put("prompt", prompt);
        body.put("n", 1);
        body.put("size", "1024x1024");

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            JSONObject jsonResponse = new JSONObject(response.toString());
            String imageUrl = jsonResponse.getJSONArray("data").getJSONObject(0).getString("url");
            return imageUrl; // URL da imagem gerada
        } catch (IOException e) {
            // Lê a resposta de erro da API
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            StringBuilder errorResponse = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                errorResponse.append(responseLine.trim());
            }
            throw new IOException("Erro da API OpenAI: " + errorResponse.toString());
        }
    }

    private final String text = "Pense como especialista em marketing para empresas de tecnologias: Uma imagem moderna no estilo Instagram promovendo uma empresa de software chamada \"Punk Code Solution\". cores hex(#d7ebc1, #eaeaea, #0d4318 ) cores (preto, azul e magenta) e elementos como código digital, interfaces de usuário elegantes, telas de computador e uma equipe criativa de desenvolvedores trabalhando em conjunto. O logotipo ou o nome da marca \"Punk Code Solution\" deve ser exibido com destaque em uma fonte em negrito e estilo tecnológico. Incorpore um texto atraente como \"Transformando Ideias em Código\". Pronto para Construir o Futuro Conosco? Soluções Inovadoras para um Mundo Digital – Visite punkcodesolution.com.br. Vamos Criar o Próximo Grande Sucesso. Junte-se a nós em punkcodesolution.com.br. Onde a Tecnologia de Ponta Encontra Mentes Criativas. Descubra Mais em punkcodesolution.com.br. A vibe geral deve ser inovadora, ousada e de alta tecnologia — perfeita para uma startup de tecnologia. Inclua uma chamada para ação para visitar o site. Formato: quadrado (1:1), estilo de postagem do Instagram.";


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

    public static String hiperlink = "https://www.punkcodesolution.com.br";

    private static final String[] textLegend = {
            "Acesse nosso site: punkcodesolution.com.br",
            "Transformando Ideias em Código. Pronto para Construir o Futuro Conosco? –  Visite " + hiperlink,
            "Soluções Inovadoras para um Mundo Digital – Visite " + hiperlink,
            "Vamos Criar a Próxima Grande Coisa. Junte-se a Nós em " + hiperlink,
            "Onde Tecnologia de Ponta Encontra Mentes Criativas. Descubra Mais em " + hiperlink
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