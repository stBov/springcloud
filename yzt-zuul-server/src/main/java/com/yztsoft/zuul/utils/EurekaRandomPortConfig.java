package com.yztsoft.zuul.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.net.InetAddress;
import java.net.Socket;

@Slf4j
public class EurekaRandomPortConfig implements EmbeddedServletContainerCustomizer {
	@Autowired
	private EurekaConfig config;
	@Autowired
	private ApplicationInfo info;
	private int port;//java -jar target/xxx.jar --eureka.random.portBase=9900启动命令修改端口号
	private static String path = "/cas/neon/port";
	private static String fileName;
	private static final int RETRYTIMES = 100;

	@Override
	public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
		/*SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//设置日期格式
	    System.out.print("begin:"+df.format(new Date())+"\n");*/
		boolean flag = true;
		int num = 0;
		while (flag == true && num < RETRYTIMES) {
			port = RandomUtils.nextInt(config.getPortRange()) + config.getPortBase();

			try {
				InetAddress address = InetAddress.getByName("127.0.0.1");
				Socket s = new Socket(
						address/* InetAddress.getLocalHost().getHostAddress() */, port); // 建立一个Socket连接
				s.close();
				flag = true;
				num++;
				log.info("port is using:" + port);
			} catch (Exception e) {
				try {
					Socket serverSocket = new Socket("0.0.0.0", port); // 建立一个Socket连接
					serverSocket.close();
					flag = true;
					num++;
					log.info("port is using:" + port);
				} catch (Exception ex) {
					/*
					 * File tmpFile = null; try { tmpFile =
					 * File.createTempFile("none", "tmp");
					 * tmpFile.deleteOnExit();
					 * log.info(tmpFile.getAbsolutePath()); } catch (IOException
					 * e1) { log.info("can not create temp file :" +
					 * e1.getMessage()); e1.printStackTrace(); }
					 */
					if (createFile(port, info.getName().toLowerCase()) == true) {
						flag = false;
						log.info("port is idle:" + port);
					} else {
						flag = true;
						num++;
						log.info("port is using:" + port);
					}
				}
			}
		}
		if (flag == true) {
			log.info("Run out of retries!");
		} else {
			configurableEmbeddedServletContainer.setPort(port);
			log.info("start port:" + port);

		}
//		System.out.print("end:"+df.format(new Date())+"\n");
	}

	/*
	 * @PreDestroy public void destory() {
	 * 
	 * log.info("我被销毁了、、、、、我是用的@PreDestory的方式、、、、、、");
	 * log.info("我被销毁了、、、、、我是用的@PreDestory的方式、、、、、、"); }
	 */
	public static boolean createFile(int port, String applicationName) {
		Boolean bool = false;
		File f = new File(path);

		if (!f.exists()) {
			f.mkdirs();// 创建目录
		}
		fileName = port + "_" + applicationName + ".ini";// 文件路径+名称+文件类型

		try {

			File filePath = new File(path);
			File[] tempList = filePath.listFiles();

			for (int i = 0; i < tempList.length; i++) {
				if (tempList[i].isFile()) {
					if (tempList[i].toString().contains(port + "")) {
						bool = false;
						return bool;
					}
				}

			}

			File file = new File(path, fileName);
			file.createNewFile();
			bool = true;

		} catch (Exception e) {
			bool = false;
			log.info("create file fail");
		}

		return bool;
	}

	@Bean
	public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils) {

		EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(inetUtils);
		config.setInstanceId(config.getIpAddress() + ":" + port);
		config.setNonSecurePort(port);
		config.setPreferIpAddress(true);
		config.setIpAddress(config.getIpAddress());
		return config;
	}
}