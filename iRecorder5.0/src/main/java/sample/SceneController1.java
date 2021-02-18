package sample;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class SceneController1 {
    /* 伪全局变量 */

    private JSONObject dataJsonObject; // 读API返回的JSON数据
    private String title; // 视频的标题
    private int pubdateUnixTimestamp; // 视频的投稿时间，用于计算总增速
    private Timer timer; //计时器，用于每分钟执行一个method，退出时要关闭它
    private DataRecord dataRecord; // 用于获取数据、计算速度、保存数据
    


    /* 公共功能 */
    public static String readApi(String urlPath) throws IOException {
        URL url = new URL(urlPath);
        InputStream iis = url.openStream();
        String s = new String(iis.readAllBytes());
        iis.close();
        return s;
    }
    
    @FXML
    private Label[] datePlayLabel;

    @FXML
    private Label labelInfo;

    @FXML
    private Label midLabel2;

    @FXML
    private AnchorPane fristPagePane;

    @FXML
    private Label midLabel1;

    @FXML
    private Label descLabel;

    @FXML
    private Label bvidLabel;

    @FXML
    private Label ownerNameLabel;

    @FXML
    private Label tnameLabel;

    @FXML
    private AnchorPane anchorPane1;

    @FXML
    private ImageView imageView1;

    @FXML
    private Label label1;

    @FXML
    private Label bigLabelTitle;

    @FXML
    private Label label2;

    @FXML
    private ImageView imageView2;

    @FXML
    private ImageView picImageView;

    @FXML
    private ImageView ownerFaceImageView;

    @FXML
    private AnchorPane mainPagePane;

    @FXML
    private TextField textField2;

    @FXML
    private TextField textField1;

    @FXML
    private Label bigLabel1;

    @FXML
    private Label otherInfoLabel;

    @FXML
    private Label pubtimeLabel;

    @FXML
    private Button button1;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @FXML
    void initialize() {
        assert labelInfo != null : "fx:id=\"labelInfo\" was not injected: check your FXML file 'window.fxml'.";
        assert fristPagePane != null : "fx:id=\"fristPagePane\" was not injected: check your FXML file 'window.fxml'.";
        assert bvidLabel != null : "fx:id=\"bvidLabel\" was not injected: check your FXML file 'window.fxml'.";
        assert tnameLabel != null : "fx:id=\"tnameLabel\" was not injected: check your FXML file 'window.fxml'.";
        assert anchorPane1 != null : "fx:id=\"anchorPane1\" was not injected: check your FXML file 'window.fxml'.";
        assert imageView1 != null : "fx:id=\"imageView1\" was not injected: check your FXML file 'window.fxml'.";
        assert label1 != null : "fx:id=\"label1\" was not injected: check your FXML file 'window.fxml'.";
        assert bigLabelTitle != null : "fx:id=\"bigLabelTitle\" was not injected: check your FXML file 'window.fxml'.";
        assert label2 != null : "fx:id=\"label2\" was not injected: check your FXML file 'window.fxml'.";
        assert imageView2 != null : "fx:id=\"imageView2\" was not injected: check your FXML file 'window.fxml'.";
        assert mainPagePane != null : "fx:id=\"mainPagePane\" was not injected: check your FXML file 'window.fxml'.";
        assert textField2 != null : "fx:id=\"textField2\" was not injected: check your FXML file 'window.fxml'.";
        assert textField1 != null : "fx:id=\"textField1\" was not injected: check your FXML file 'window.fxml'.";
        assert bigLabel1 != null : "fx:id=\"bigLabel1\" was not injected: check your FXML file 'window.fxml'.";
        assert otherInfoLabel != null : "fx:id=\"otherInfoLabel\" was not injected: check your FXML file 'window.fxml'.";
        assert button1 != null : "fx:id=\"button1\" was not injected: check your FXML file 'window.fxml'.";

        /* 初始化需要保存存档、图片的目录 */
        String icedataPath = System.getenv("APPDATA") + "/icedata/";
        File fileCreator = new File(icedataPath);
        if(!fileCreator.exists()) {
            fileCreator.mkdirs();
            fileCreator = new File(icedataPath + "/cache/");
            fileCreator.mkdirs();
            fileCreator = new File(icedataPath + "/storage/");
            fileCreator.mkdirs();
        }

        anchorPane1.setPrefHeight(536);
        fristPagePane.setLayoutY(0);

    }

    /**
     * 随着文本框textField中被键入文本，检查是否是AV号、BV号。 如果是链接，要试图转化成AV号、BV号。
     * 如果是有效的AV号、BV号，需要显示目标视频的标题。
     */
    @FXML
    void textField1Typed() {

        String text = textField1.getText();
        if (text.length() < 2) {
            return;
        }
        String url = "";

        // 以下几行对链接地址进行预处理，试图转化成AV号或BV号。
        int index = text.indexOf("?");
        if (index != -1) {
            text = text.substring(0, index);
            text = text.replace("www.bilibili.com/video/", "");
            text = text.replace("https://", "");
            text = text.replace("http://", "");
            text = text.replace("/", "");
        }

        if (text.length() < 2) {
            labelInfo.setText("");
            textField2.setText("");
            button1.setDisable(true);
            return;
        }

        if ((text.charAt(0) == 'B' || text.charAt(0) == 'b')
                && (text.charAt(1) == 'V' || text.charAt(1) == 'v')) {
            url = "http://api.bilibili.com/x/web-interface/view?bvid=" + text;
        } else if ((text.charAt(0) == 'A' || text.charAt(0) == 'a')
                && (text.charAt(1) == 'V' || text.charAt(1) == 'v')) {
            url = "http://api.bilibili.com/x/web-interface/view?aid=" + text.substring(2);
        } else if (text.equals(String.valueOf(Integer.valueOf(text)))) {
            url = "http://api.bilibili.com/x/web-interface/view?aid=" + text;
        } else {
            // 无法识别
            labelInfo.setText("");
            textField2.setText("");
            button1.setDisable(true);
        }

        if (!url.isEmpty()) {
            try {
                String fileContext = readApi(url);
                JSONObject j = JSON.parseObject(fileContext);
                int code = (int) j.get("code");
                if (code == -404) {
                    labelInfo.setText("");
                    textField2.setText("");
                    button1.setDisable(true);
                } else {
                    this.dataJsonObject = j;
                    // ↑ 保存为伪全局变量，以便按钮点击时不要再获取一次
                    this.title = j.getJSONObject("data").getString("title");
                    String bvid = j.getJSONObject("data").getString("bvid");
                    String storagePath = System.getenv("APPDATA") + "\\icedata\\storage\\data_" + bvid + "_" + System.nanoTime() % 1000000 + ".csv";
                    labelInfo.setText(title);
                    textField2.setText(storagePath);
                    button1.setDisable(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当【启动】按钮被按下之后，应当进行的事务。 1) 给切换到的新页面初始化各项数据 2) 初始化数据记录类的实例 3) 开始计时循环
     */
    @FXML
    void button1Checked(ActionEvent event) {
        button1.setDisable(true); // 避免再次被点击
        initializeGUI();
        periodicEvents();

        timer = new Timer();
        TimerTask refresh = new TimerTask() {
            public void run() {
                periodicEvents();
            }
        };
        timer.schedule(refresh, 1000, 60000);
        Holder.map.put("timer", timer);
        System.out.println("【事件】[启动]按钮被按下");
    }

    /**
     * 初始化第二页的用户界面
     */
    void initializeGUI() {
        JSONObject data = dataJsonObject.getJSONObject("data");
        bigLabelTitle.setText(this.title);
        if (this.title.charAt(0) == '【') {
            bigLabelTitle.setLayoutX(38);
        }
        if (this.title.charAt(0) == '《') {
            bigLabelTitle.setLayoutX(43);
        }

        // 初始化窗口眉部的控件
        pubdateUnixTimestamp = (int) data.get("pubdate");
        String bvid = data.getString("bvid"); // 因为bvid在后文还会用到，所以声明变量
        bvidLabel.setText(" " + bvid); // 空一格为了美观
        
        tnameLabel.setText(data.getString("tname"));
        String otherInfoString = "";
        if (data.getInteger("videos") > 1) {
            otherInfoString = "包含" + data.getInteger("videos") + "个视频\t";
        }
        if (data.getInteger("copyright") != 1) {
            otherInfoString = otherInfoString + "[搬运]";
        }
        otherInfoLabel.setText(otherInfoString);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String pubtimeString = dateFormat.format(data.getInteger("pubdate") * 1000L);
        
        pubtimeLabel.setText(pubtimeString);

        datePlayLabel = new Label[21];

        Font font = Font.font("Microsoft Yahei", 14);
        Paint paint = Paint.valueOf("#334455");
        for (int i = 0; i < 21; i++) {
            datePlayLabel[i] = new Label();
            datePlayLabel[i].setLayoutY((i % 7) * 40 + 200.0);
            datePlayLabel[i].setLayoutX((i / 7) * 180.0 + 250.0);
            datePlayLabel[i].setPrefWidth(180);
            datePlayLabel[i].setMaxWidth(180);
            datePlayLabel[i].setAlignment(Pos.CENTER_RIGHT);
            datePlayLabel[i].setFont(font);
            datePlayLabel[i].setTextFill(paint);
            switch (i) {
                case 0 -> datePlayLabel[i].setText("总量");
                case 7 -> datePlayLabel[i].setText("整体速率");
                case 14 -> datePlayLabel[i].setText("瞬时速率");
                default -> datePlayLabel[i].setText(i >= 7 ? "15920 /h" : "15920");
            }
            mainPagePane.getChildren().addAll(datePlayLabel[i]);
        }

        System.out.println("【提示】开始加载封面");
        // 下载封面并设置封面显示
        String urlPath1 = data.getString("pic");
        String localPath1 = ImageHandler.getPicImage(urlPath1, bvid);
        Image image1 = new Image("file:" + localPath1);
        picImageView.setImage(image1);

        // 封面需要圆角化
        Rectangle rectangle1 = new Rectangle(180, 112.5); //图幅180 * 121
        rectangle1.setArcHeight(12);
        rectangle1.setArcWidth(12);
        picImageView.setClip(rectangle1);

        System.out.println("【提示】开始加载头像");
        // 下载头像并设置头像显示
        JSONObject owner = data.getJSONObject("owner");
        int uid = (int) owner.get("mid");
        String urlPath2 = owner.getString("face");
        String localPath2 = ImageHandler.getOwnerFace(urlPath2, uid);
        ownerFaceImageView.setImage(new Image("file:" + localPath2));

        // 头像需要圆角化
        Rectangle rectangle2 = new Rectangle(24, 24); //图幅24 * 24
        rectangle2.setArcHeight(24);
        rectangle2.setArcWidth(24);
        ownerFaceImageView.setClip(rectangle2);

        ownerNameLabel.setText(owner.getString("name"));
        descLabel.setText(data.getString("desc"));
        descLabel.setTooltip(new Tooltip(data.getString("desc")));
        bvidLabel.setTooltip(new Tooltip("AV" + data.getJSONObject("stat").get("aid")));

        dataRecord = new DataRecord(bvid, textField2.getText());
        fristPagePane.setLayoutY(600);
    }

    /**
     * 周期事件 每分钟都要调用一次本事件。 每次事件中需要刷新数据，并且更新GUI。
     */
    private void periodicEvents() {
        dataRecord.refreshData();
        refreshGUI(dataRecord.getVelocityValues(), dataRecord.getCurrentValues());
    }

    /**
     * 本方法必须在initializeGUI()之后才能调用。 本方法需要的参数，均为dataRecord相应方法的返回值。
     * @param velocityValues 六项数据的增速
     * @param currentValues 六项数据的当前值
     */
    private void refreshGUI(double[] velocityValues, int[] currentValues) {
        /*
          currentValues 六项数据的当前值 velocityValues 六项数据的瞬时速度 totalValues 六项数据的总速度
         */
        double[] totalValues = new double[6];
        int currentUnixTimestamp = (int) (System.currentTimeMillis() / 1000L);
        double deltaTime = (double) currentUnixTimestamp - (double) pubdateUnixTimestamp;
        for (int i = 0; i < totalValues.length; i++) {
            totalValues[i] = currentValues[i] / deltaTime * 60.0; //此处乘60，把单位从每秒华为每分
        }
        Platform.runLater(() -> {
            for (int i = 1; i <= 6; i++) {
                datePlayLabel[i].setText(doubleToString(currentValues[i - 1], false));
            }
            for (int i = 8; i <= 13; i++) {
                datePlayLabel[i].setText(doubleToString(totalValues[i - 8], true));
            }
            for (int i = 15; i <= 20; i++) {
                datePlayLabel[i].setText(doubleToString(velocityValues[i - 15], true));
            }
        });
    }

    /**
     * 把double型的数据转化为double型
     * @param d 欲转换的double数据
     * @param isVelocity 是不是增速型，如果是，需要添加单位“/h”，并保留两位小数。
     * @return 返回一个String
     */
    private String doubleToString(double d, boolean isVelocity) {
        if (isVelocity) {
            DecimalFormat df = new DecimalFormat("#,##0.0");
            return df.format(d * 60.0) + " /h";
        } else {
            DecimalFormat df = new DecimalFormat("#,###");
            return df.format(d);
        }
    }
}

/**
 * 本类用于下载封面、头像
 */
class ImageHandler {
    private ImageHandler() {

    }

    /**
     * 下载图片
     * 本method只被本类其它method调用，故为private
     * @param urlPath 图片的URL地址
     * @param localPath 本地的保存路径
     * @throws IOException 如果下载失败，就抛出IOException
     */
    private static void downloadImage(String urlPath, String localPath) throws IOException{
        URL url = new URL(urlPath);
        InputStream inputStream = url.openStream();
        FileOutputStream out = new FileOutputStream(localPath);
        out.write(inputStream.readAllBytes());

        inputStream.close();
        out.close();
    }

    /**
     * 下载封面
     * @param urlPath 封面的URL地址，由JSON文件给出
     * @param bvid 视频的BV号
     * @return 返回一个String，用来表示下载后的图片的本地路径
     */
    public static String getPicImage(String urlPath, String bvid) {
        String localPath = System.getenv("APPDATA") +  "/icedata/cache/pic_" + bvid + ".png";
        File file = new File(localPath);
        if (!file.exists()) {
            file.getParentFile().mkdir();
            try {
                downloadImage(urlPath, localPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return localPath;
    }

    /**
     * 下载头像
     * @param urlPath 封面的URL地址，由JSON文件给出
     * @param uid 用户的UID
     * @return 返回一个String，用来表示下载后的图片的本地路径
     */
    public static String getOwnerFace(String urlPath, int uid) {
        String localPath = System.getenv("APPDATA") +  "/icedata/cache/face_" +  + uid + ".png";
        File file = new File(localPath);
        if (!file.exists()) {
            try {
                downloadImage(urlPath, localPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return localPath;
    }

}

/**
 * 数据处理方面的类
 */
class DataRecord {
    private final String bvid;
    private final String storagePath;
    private final Queue<Integer> queueView = new LinkedList<>();
    private final Queue<Integer> queueFavo = new LinkedList<>();
    private final Queue<Integer> queueLike = new LinkedList<>();
    private final Queue<Integer> queueCoin = new LinkedList<>();
    private final Queue<Integer> queueReply = new LinkedList<>();
    private final Queue<Integer> queueShare = new LinkedList<>();
    private final double[] velocity = new double[6]; // 用于存放六个属性的增速
    private final int[] current = new int[6]; // 用于存放六个属性的当前值

    public DataRecord(String bvid, String storagePath) {
        this.bvid = bvid;
        this.storagePath = storagePath;
    }

    /**
     * 刷新数据 
     * 1) 从B站API读出文本，转化为JSONObject对象，并将6个数据加入队列。 
     * 2) 保存新读出来的数据到文件storagePath。 
     * 3) 算六项数据的增速，保存在velocity[]中。
     */
    public void refreshData() {
        String url = "http://api.bilibili.com/x/web-interface/view?bvid=" + bvid;
        try {
            String fileContext = SceneController1.readApi(url);
            JSONObject j = JSON.parseObject(fileContext);
            int code = (int) j.get("code");
            if (code == 0) {
                JSONObject stat = j.getJSONObject("data").getJSONObject("stat");
                current[0] = (int) stat.get("view");
                current[1] = (int) stat.get("favorite");
                current[2] = (int) stat.get("like");
                current[3] = (int) stat.get("coin");
                current[4] = (int) stat.get("reply");
                current[5] = (int) stat.get("share");
                queueView.offer(current[0]);
                queueFavo.offer(current[1]);
                queueLike.offer(current[2]);
                queueCoin.offer(current[3]);
                queueReply.offer(current[4]);
                queueShare.offer(current[5]);

                saveData();
            }
            if (queueView.size() > 10) {
                queueView.poll();
                queueFavo.poll();
                queueLike.poll();
                queueCoin.poll();
                queueReply.poll();
                queueShare.poll();
            }

            calculateEachVelocity();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在refreshData()方法内调用本方法。 本方法不希望从外面直接调用，故而为private。
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveData() {
        File file = new File(storagePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
                Writer w;
                w = new FileWriter(file);
                w.write("time,views,favo,likes,coin,reply,share\n");
                // views和likes加s的原因：在SQL里，VIEW和LIKE是保留字，为了方便数据导入SQL，并且不引发歧义。
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Writer w;
                w = new FileWriter(file, true); // 因为是追加写入，所以是true
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String string = dateFormat.format(System.currentTimeMillis()) + ',' + current[0] + ',' + current[1]
                        + ',' + current[2] + ',' + current[3] + ',' + current[4] + ',' + current[5] + '\n';
                w.write(string);
                w.close();
                System.out.println("【提示】已写出数据\t" + storagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 计算增速 使用差值法对队列里的数据进行拟合，计算器增长速率，计算结果保存在velocity[]里。
     */
    private void calculateEachVelocity() {
        velocity[0] = calculateVelocity(queueView);
        velocity[1] = calculateVelocity(queueFavo);
        velocity[2] = calculateVelocity(queueLike);
        velocity[3] = calculateVelocity(queueCoin);
        velocity[4] = calculateVelocity(queueReply);
        velocity[5] = calculateVelocity(queueShare);
    }

    /**
     * 计算一个队列的增速，仅被calculateEachVelocity()调用。 
     * @param queue 用于计算的队列，其长度不超过10，不过超过了也没关系。
     * @return 返回值为计算结果。
     */
    private double calculateVelocity(Queue<Integer> queue) {
        if (queue.size() <= 1) {
            return 0.0;
        } else if (queue.size() < 10) {
            Integer[] nums = queue.toArray(new Integer[0]);
            return (nums[nums.length - 1] - nums[0]) / (nums.length - 1.0);
        }
        // queue.size() == 10:
        Integer[] nums = queue.toArray(new Integer[0]);
        int sum1 = nums[0] + nums[1] + nums[2] + nums[3] + nums[4];
        int sum2 = nums[5] + nums[6] + nums[7] + nums[8] + nums[9];
        return (sum2 - sum1) / 25.0;
    }

    public double[] getVelocityValues() {
        return this.velocity;
    }

    public int[] getCurrentValues() {
        return this.current;
    }
}
