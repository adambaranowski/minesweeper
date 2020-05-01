package pl.adambaranowski.minesweeper.functions;

public interface RequestSender {

    default String sendRequest(String request) {

        System.out.println(request);
        String response = WebSocketConnector.getInstance().dataTransfer(request);
        System.out.println(response);

        return response;
    }
}
