package com.jd.blockchain.gateway.boot;

import com.jd.blockchain.gateway.GatewayServerBooter;
import utils.ConsoleUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GatewayBooter {

	public static void main(String[] args) {
		try {
			GatewayServerBooter.main(args);
		} catch (Exception e) {
			ConsoleUtils.error("Gateway start error!", e);
		}
	}
}
