package br.ufma.ecp;

import static br.ufma.ecp.token.TokenType.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
public class App 
{

    private static String fromFile() {
        File file = new File("Main.jack");

        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
            String textoDoArquivo = new String(bytes, "UTF-8");
            return textoDoArquivo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    } 

    public static void main( String[] args ) {
        String input = """
                class Main {
                   field int x,y;
                   static boolean b;
                   static int a = 20;
                   field int x = 10;
                   
                   constructor Square new() {
                         return this;
                   }
                   
                   constructor Square new(int Ax, int Ay, int Asize) {
                         let x = Ax;
                         let y = Ay;
                         let size = Asize;
                         do draw();
                         do draw.drawCirculo();
                         return this;
                   }
                      
                   method void moveLeft(int Ax, int Ay, int Asize) {
                        do draw();
                        let x = Ax;
                        let y = Ay;
                        do draw.drawCirculo();
                        let size = Asize;
                        return;
                   }
                   
                   
                   method void incSize() {
                         if (((y + size) < 254) & ((x + size) < 510)) {
                            do erase();
                            let size = size + 2;
                            do draw();
                         }
                         return;
                      }
                }
                """;
        Parser p = new Parser(input.getBytes());
        p.parser();
        System.out.println(p.XMLOutput());
    }
}
