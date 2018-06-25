import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.IOException;

public class TestUploadFile {
    @Test
    public void uploadFile() throws IOException, MyException {
        //1.向工程中添加jar包
        //2.创建一个配置文件、配置tracker的服务器地址
        //3.加载配置文件
        //4.创建一个TrackerCilent对象
        //5、使用TrackerClient对象获得TrackerServer对象
        //6、创建一个StorageServer的null
        //7.创建一个StorageClient对象、TrackerServer，StorageServer两个参数
        //8/使用StorageClient对象上传文件
        ClientGlobal.init("S:/develop/IdeaProjests/shopping/taotao-manager-web/src/main/resources/resource/client.conf");
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        StorageServer storageServer = null;
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        String[] filePath = storageClient.upload_file("F:/pictures/1.jpg","jpg",null);
        for (String str:
             filePath) {
            System.out.println(str);
        }

    }
}
