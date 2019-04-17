import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        Scanner scansocket;
        Socket socket;
        FileWriter arq;
        OutputStream envio;
        String linha, endereco, url, host, dir, arqurl, requisicao;
        String[] partes;
        List<String> buffer;
        int porta = 80;
        int inicio = 0;
        int fim = 0;
        boolean erro;

        linha = scan.nextLine();

        partes = linha.split(" ");
        endereco = partes[0];

        if (partes.length == 2) {
            try {
                porta = Integer.parseInt(partes[1]);
            } catch (NumberFormatException e) {
                System.out.println("Você não digitou um número na porta!\nFinalizando navegador!");
                System.exit(0);
            }
        } else if (partes.length == 0) {
            System.out.println("Você digitou algo errado...\nFinalizando navegador!");
            System.exit(0);
        }

        url = endereco.replace("http://", "").replace("https://", "");

        host = (url.substring(0, url.indexOf("/", 0)));
        dir = (url.substring(url.indexOf("/", 0)));
        arqurl = (url.substring(url.lastIndexOf("/") + 1));

        socket = new Socket(host, porta);

        if (socket.isConnected()) {

            System.out.println("Conectado a " + socket.getInetAddress() + "\n\n");

            if (url.charAt(url.length() - 1) == '/') {
                requisicao = "GET " + dir + "/ HTTP/1.1\r\n" + "Host: " + host + "\r\n" + "\r\n";
                arqurl = "index.html";
            }
            else {
                requisicao = "GET " + dir + " HTTP/1.1\r\n" + "Host: " + host + "\r\n" + "\r\n";}
            envio = socket.getOutputStream();
            
            envio.write(requisicao.getBytes());
            envio.flush();

            scansocket = new Scanner(socket.getInputStream());
            buffer = new ArrayList<>();
            
            while (scansocket.hasNext()) {
                linha = scansocket.nextLine();
                buffer.add(linha);
            }
            
            erro = false;
            
            for (int i = 0; i < buffer.size(); i++) {
                if(buffer.get(i).contains("erro") || buffer.get(i).contains("ERRO")) {
                    erro = true;
                }
                if (buffer.get(i).contains("DOCTYPE")) {
                    inicio = i;
                }
                if (buffer.get(i).contains("</html>")) {
                    fim = i;
                    break;
                }
            }

            for (int i = 0; i < inicio; i++) {
                System.out.printf("%s\n", buffer.get(i));
            }
            
            if(erro) {
                System.exit(0);
            }
            
            arq = new FileWriter(new File(arqurl));

            for (int i = inicio; i <= fim; i++) {
                arq.write(buffer.get(i));
                arq.write("\n");
            }

            arq.flush();
            arq.close();
        }
    }
}