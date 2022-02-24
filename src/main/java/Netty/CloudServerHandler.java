package Netty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import GeneralClasses.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CloudServerHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path currentDir;

    private AuthService authService;

    private String login;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // init client dir
        AuthService.connection();
        currentDir = Paths.get("C:\\Java\\cloude\\data");
        sendList(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getType()) {

            case FILE_REQUEST:
                processFileRequest((FileRequest) cloudMessage, ctx);
                break;

            case FILE:
                processFileMessage((FileMessage) cloudMessage, ctx);
                break;

            case DELETE:
                System.out.println("DELETE");
                processDelete((Delete) cloudMessage);
                sendList(ctx);
                break;

            case CHANGE_OUT_SERVER_DIR:     //Выход из текущей директории
                if (currentDir.getParent() != null) {
                    currentDir = currentDir.getParent();
                    sendList(ctx);
                }
                break;

            case CHANGE_IN_SERVER_DIR:  //Вход в указанную директорию
                processChangeInServerDir((ChangeInServerDir) cloudMessage);
                sendList(ctx);
                break;

            case CREATE_NEW_PATH:   //Создание новой папки
                processNewPathCreating((CreateNewPathMessage) cloudMessage);
                sendList(ctx);
                break;

            case LOGIN_AND_PASSWORD_MESSAGE:    //Клиент делает попытку авторизации
                processOfAccess((LoginAndPasswordMessage) cloudMessage, ctx);
                break;

            case REGISTRY_LOGIN_AND_PASSWORD_MESSAGE:   //Клиент делает попытку регистрации
                processOfRegistry((RegistryLoginAndPasswordMessage) cloudMessage, ctx);
                break;
        }
    }

    private void processOfRegistry(RegistryLoginAndPasswordMessage cloudMessage, ChannelHandlerContext ctx) throws SQLException, IOException {
        boolean Access = AuthService.setNewUser(
                cloudMessage.getJsonMessage().get("login").toString(),
                cloudMessage.getJsonMessage().get("password").toString(),
                cloudMessage.getJsonMessage().get("email").toString()
        );

        if (Access) {
            processNewPathCreating(cloudMessage.getJsonMessage().get("login").toString());
            ctx.writeAndFlush(new RegistrySuccessfulMessage());
        } else {
            ctx.writeAndFlush(new RegistryDeniedMessage());
        }
    }

    private boolean processOfAccess(LoginAndPasswordMessage cloudMessage, ChannelHandlerContext ctx) throws IOException {
        boolean Access = AuthService.getAccess(
                cloudMessage.getJsonMessage().get("login").toString(),
                cloudMessage.getJsonMessage().get("password").toString()
        );

        if (Access) {
            currentDir = Paths.get("C:\\Java\\cloude\\data\\" + cloudMessage.getJsonMessage().get("login").toString());
            login = cloudMessage.getJsonMessage().get("login").toString();
            ctx.writeAndFlush(new AccessedMessage());
            sendList(ctx);
            return true;
        } else {
            System.out.println("DENIED");
            ctx.writeAndFlush(new AccessDeniedMessage());
        }
        return false;
    }

    private void processNewPathCreating(CreateNewPathMessage cloudMessage) throws IOException {
        String dirName = currentDir + "\\" + cloudMessage.getDirName();
        Path path = Paths.get(dirName);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
            System.out.println("Directory created");
        } else {
            System.out.println("Directory already exists");
        }
    }

    private void processNewPathCreating(String pathName) throws IOException {
        String dirName = currentDir + "\\" + pathName;
        Path path = Paths.get(dirName);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
            System.out.println("Directory created");
        } else {
            System.out.println("Directory already exists");
        }
    }

    private void processDelete(Delete cloudMessage) throws IOException {
        System.out.println("DELETE");
        Files.delete(currentDir.resolve(cloudMessage.getFileName()));
    }

    private void sendList(ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new ListMessage(currentDir));
    }

    private void processChangeInServerDir(ChangeInServerDir cloudMessage) throws IOException {
        currentDir = Paths.get(currentDir.toString() + "\\" + cloudMessage.getFileName());
    }

    private void processFileMessage(FileMessage cloudMessage, ChannelHandlerContext ctx) throws IOException, SQLException {
        Files.write(currentDir.resolve(cloudMessage.getFileName()), cloudMessage.getBytes());
        System.out.println(currentDir+ "\\" +cloudMessage.getFileName());
        AuthService.setNewFile(cloudMessage.getFileName(), AuthService.findUserId(login), currentDir+ "\\" +cloudMessage.getFileName());
        sendList(ctx);
    }

    private void processFileRequest(FileRequest cloudMessage, ChannelHandlerContext ctx) throws IOException {
        Path path = currentDir.resolve(cloudMessage.getFileName());
        ctx.writeAndFlush(new FileMessage(path));
    }
}
