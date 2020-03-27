package com.test;

import java.util.*;

import com.am.*;
import com.primary.*;

public class Test {
	
	
			
			public static void main(String[] args) {
				PrimarySelection ps = new PrimarySelection(null);
				String s1 = "adaway/ui/tcpdump";
				String s2 = "ios/org/adaway/ui/tcpdump/help";
				String s3 = "org/adaway/ui/tcpdump/help/ddd";
				System.out.println(ps.similar(s1,s3));
				System.out.println(ps.similar(s2,s3));
			}
			

}
