package com.mystudy.ex04_printwriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PrintWriter_Writer_Exam {

	public static void main(String[] args) {
		// PrintWriter <-- FileOutputStream <- File 
		PrintWriter pw = null;
		
		try {
			// 방식1 : 생성된 객체를 변수에 모두 저장
			File file = new File("file/pw_out.txt"); 
			FileWriter fw = new FileWriter(file); 
			pw = new PrintWriter(fw); 
			
			// 방식2 : 한번에 처리(최종 사용객체만 저장 사용)
			pw = new PrintWriter(new FileWriter(new File("file/pw_out2.txt")));
			pw = new PrintWriter(new FileWriter("file/pw_out2.txt"));
			
			pw.write("안녕하세요! \n");  // \n <- 줄바꿈
			pw.write("점심시간 후 오후~~\n");
			
			pw.print("지금 자바 공부중!!"); // print문은 줄바꿈이 안 되고 옆으로 출력됨.
			pw.print("아직은 안 졸려요!");
			pw.println(); // .println(); 줄바꿈 처리!
			
			pw.println("조금 지나면 졸릴수도 있다!!"); // println을 사용해서 출력 후 줄바꿈까지 된다.
			pw.println("끝나려면 4시간 남았다.");
			
			pw.printf("%10s %10s %10s %n", "이제", "20분", "남았다"); 
			// s는 string이다. 10자리 10자리 10자리
			pw.printf("%-50s%n", "18분 정도 남았습니다"); // 좌측정렬
			pw.printf("%50s%n", "17분 정도 남았습니다"); // 우측정렬
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) pw.close(); // pw가 null이 아니면 close 해라.
		}
		
		
	}

}
