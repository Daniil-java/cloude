package Netty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import GeneralClasses.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CloudServerHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path rootDir;
    private Path currentDir;
    private AuthService authService;
    private String login;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // init client dir
        AuthService.connection();
        rootDir = Paths.get("C:\\Java\\cloude\\data\\users");
        currentDir = Paths.get("C:\\Java\\cloude\\data\\users");
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
                processDelete((Delete) cloudMessage, ctx);
                break;

            case CHANGE_OUT_SERVER_DIR:     //Выход из текущей директории
                processChangeOutServerDir((ChangeOutServerDir) cloudMessage, ctx);
                break;

            case CHANGE_IN_SERVER_DIR:  //Вход в указанную директорию
                processChangeInServerDir((ChangeInServerDir) cloudMessage, ctx);
                break;

            case CREATE_NEW_PATH:   //Создание новой папки
                processNewPathCreating((CreateNewPathMessage) cloudMessage, ctx);
                break;

            case LOGIN_AND_PASSWORD_MESSAGE:    //Клиент делает попытку авторизации
                processOfAccess((LoginAndPasswordMessage) cloudMessage, ctx);
                break;

            case REGISTRY_LOGIN_AND_PASSWORD_MESSAGE:   //Клиент делает попытку регистрации
                processOfRegistry((RegistryLoginAndPasswordMessage) cloudMessage, ctx);
                break;

            case SHARE:
                processOfSharing((ShareMessage) cloudMessage, ctx);
                break;

            case SHARE_FILE_REQUEST:
                processOfSharingFileRequest((ShareFileRequest) cloudMessage, ctx);
                break;
                
            case SHARE_FILE_DOWNLOAD:
                processOfSharingFileDownload((ShareFileDownload) cloudMessage, ctx);
                break;

            case RENAME:
                processOfRename((RenameMessage) cloudMessage, ctx);
                break;
        }
    }

    private void processOfRename(RenameMessage cloudMessage, ChannelHandlerContext ctx) throws IOException {
        String oldName = currentDir + "\\" + cloudMessage.getOldName();
        String newName = currentDir + "\\" + cloudMessage.getNewName();
        Path source = Paths.get(oldName);
        Path target = Paths.get(newName);
        System.out.println(oldName);
        System.out.println("RENAME");
        Files.move(source, source.resolveSibling(cloudMessage.getNewName()));
        sendList(ctx);
    }

    private void processOfSharingFileDownload(ShareFileDownload cloudMessage, ChannelHandlerContext ctx) throws IOException {
        Path path = rootDir.resolve(cloudMessage.getDirName());
        ctx.writeAndFlush(new ShareFileMessage(path));
    }

    private void processOfSharingFileRequest(ShareFileRequest cloudMessage, ChannelHandlerContext ctx) throws SQLException, IOException {
        List<String> urls = AuthService.getSharingFiles(AuthService.findUserId(login));
        System.out.println(urls);
        ctx.writeAndFlush(new ShareListMessage(urls));
    }

    private void processOfSharing(ShareMessage cloudMessage, ChannelHandlerContext ctx) throws SQLException {   //JSON содержит логин получателя
        boolean success = AuthService.shareFile(
                AuthService.findUserId(cloudMessage.getJsonMessage().get("login").toString()),
                currentDir + "\\" + cloudMessage.getFileName()
        );

        if (success) {
            ctx.writeAndFlush(new AccessedMessage());
        } else {
            ctx.writeAndFlush(new AccessDeniedMessage());
        }
    }

    private void processOfRegistry(RegistryLoginAndPasswordMessage cloudMessage, ChannelHandlerContext ctx) throws SQLException, IOException {
        boolean access = AuthService.setNewUser(
                cloudMessage.getJsonMessage().get("login").toString(),
                cloudMessage.getJsonMessage().get("password").toString(),
                cloudMessage.getJsonMessage().get("email").toString()
        );

        if (access) {
            System.out.println("ПРАВДА");
            processNewPathCreating(cloudMessage.getJsonMessage().get("login").toString());
            ctx.writeAndFlush(new AccessedMessage());
        } else {
            ctx.writeAndFlush(new RegistryDeniedMessage());
        }
    }

    private boolean processOfAccess(LoginAndPasswordMessage cloudMessage, ChannelHandlerContext ctx) throws IOException {
        boolean access = AuthService.getAccess(
                cloudMessage.getJsonMessage().get("login").toString(),
                cloudMessage.getJsonMessage().get("password").toString()
        );

        if (access) {
            currentDir = Paths.get("C:\\Java\\cloude\\data\\users\\" + cloudMessage.getJsonMessage().get("login").toString());
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

    private void processNewPathCreating(CreateNewPathMessage cloudMessage, ChannelHandlerContext ctx) throws IOException {
        String dirName = currentDir + "\\" + cloudMessage.getDirName();
        Path path = Paths.get(dirName);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
            sendList(ctx);
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

    private void processDelete(Delete cloudMessage, ChannelHandlerContext ctx) throws IOException, SQLException {
        System.out.println("DELETE");
        Files.delete(currentDir.resolve(cloudMessage.getFileName()));
        AuthService.deleteFile(currentDir + "\\" + cloudMessage.getFileName());
        sendList(ctx);
    }

    private void sendList(ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new ListMessage(currentDir));
        sendDir(ctx);
    }

    private void sendDir(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new ViewServerDir(currentDir.toString()));
    }

    private void processChangeInServerDir(ChangeInServerDir cloudMessage, ChannelHandlerContext ctx) throws IOException {
        currentDir = Paths.get(currentDir.toString() + "\\" + cloudMessage.getFileName());
        sendList(ctx);
    }

    private boolean processChangeOutServerDir(ChangeOutServerDir cloudMessage, ChannelHandlerContext ctx) throws IOException {
        if (currentDir.getParent().equals(Paths.get("C:\\Java\\cloude\\data\\users"))) {
            ctx.writeAndFlush(new AccessDeniedMessage());
            return false;
        }
        if (currentDir.getParent() != null) { //&& currentDir.getParent() != Paths.get("C:\\Java\\cloude\\data")
            currentDir = currentDir.getParent();
            sendList(ctx);
            return true;
        }
        return false;
    }

    private void processFileMessage(FileMessage cloudMessage, ChannelHandlerContext ctx) throws IOException, SQLException {
        Files.write(currentDir.resolve(cloudMessage.getFileName()), cloudMessage.getBytes());
        System.out.println(currentDir+ "\\" +cloudMessage.getFileName());
        AuthService.setNewFile(cloudMessage.getFileName(), AuthService.findUserId(login), currentDir+ "\\" + cloudMessage.getFileName());
        sendList(ctx);
    }

    private void processFileRequest(FileRequest cloudMessage, ChannelHandlerContext ctx) throws IOException {
        Path path = currentDir.resolve(cloudMessage.getFileName());
        ctx.writeAndFlush(new FileMessage(path));
        sendList(ctx);
    }
}
