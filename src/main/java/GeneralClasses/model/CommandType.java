package GeneralClasses.model;

public enum CommandType {
    FILE,
    FILE_REQUEST,
    SHARE_FILE_DOWNLOAD,
    SHARE_FILE,
    SHARE_FILE_REQUEST,
    LIST,                   //Gолучение списка файлов
    SHARE_LIST,
    CHANGE_OUT_SERVER_DIR,  //Вверх по директории
    CHANGE_IN_SERVER_DIR,   //Войти в директорию
    VIEW_SERVER_DIR,    //Показать директорию сервера
    CREATE_NEW_PATH,    //Создать новую папку
    DELETE,             //Удалить файл
    RENAME,
    LOGIN_AND_PASSWORD_MESSAGE,     //Запрос на подключени под логином
    REGISTRY_LOGIN_AND_PASSWORD_MESSAGE,    //Запрос на регистрацию

    ACCESSED_MESSAGE,       //Сообщение о выполнение какой-либо задачи
    ACCESS_DENIED_MESSAGE,  //

    REGISTRY_SUCCESSFUL,    //Уведомление об успешной регистрации
    REGISTRY_DENIED,

    SHARE,              //Запрос дать доступ к файлу

    EXIT;
}
