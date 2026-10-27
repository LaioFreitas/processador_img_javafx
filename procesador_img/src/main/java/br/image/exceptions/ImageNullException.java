package br.image.exceptions;

public class ImageNullException extends IllegalStateException {

    public ImageNullException() {
        super("imagem que esta tentando trata e vazia");
    }
}
