
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientSide {

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException
	{
		Scanner inp=new Scanner(System.in);
		p("Enter the IP address you want to connect to...");
		String ipAdd=inp.nextLine();

		Socket socket = new Socket(ipAdd,7227);

		//To set up streams to communicate between server and client
		ObjectOutputStream op=new ObjectOutputStream(socket.getOutputStream());
		op.flush();
		ObjectInputStream ip=new ObjectInputStream(socket.getInputStream());

		p("Welcome to FileShare , A software to help you download files through TCP sockets !!");
		String osname=System.getProperty("os.name");
		boolean ubuntu;
		if(osname.contains("Windows"))
			ubuntu=false;
		else
			ubuntu=true;
		p("You are using FileSharer on "+osname+"\n");
		if(ubuntu==false)
			p("Enter the path of the directory you want to store your downloads in..(With each '\\' replaced by '\\\\')");
			else
			p("Enter the path of the directory you want to store your downloads in..( eg. /home/sds/Desktop/Downloads )");
			String downPath=inp.nextLine();
		p("The files shared by the server are ::\n");

		//To read from server all the files shared by it
		boolean read=true;
		while(read)
		{
		String str=(String) ip.readObject();
		//To stop reading when the server stops sending strings
		if(!str.equals("-"))
		p(str);
		else
		read=false;
		}

		//Give the syntax for the software to the user
		p("");
		p("Commands ::");
		p("--> To download a file, type \"download <file name with extension>\" ");
		p("--> To get into a directory further, type \"open <folder name>\" ");
		p("--> To go back a directory, type \"back\" ");
		p("--> To exit the software without downloading anything, type \"exit\" ");


		p("");


		boolean flag=true;
		while(flag)
		{
			String command=inp.nextLine();
			op.writeObject(command);
			op.flush();

			if(command.contains("exit"))
				flag=false;
			else if(command.contains("download"))
			{
				String newPath;
				if(ubuntu)
					newPath=downPath+"/"+command.substring(9, command.length());
				else
					newPath=downPath+"\\\\"+command.substring(9, command.length());
			recieveFileFromServer(newPath,socket);
			p("File "+ command.substring(9, command.length())+" has successfully been downloaded from Server !!");
			flag=false;
			}
			else if(command.contains("open"))
			{
				//To read from server all the files shared by it
				read=true;
				while(read)
				{
				String str=(String) ip.readObject();
				//To stop reading when the server stops sending strings
				if(!str.equals("-"))
				p(str);
				else
				read=false;
				}

			}
			else if(command.contains("back"))
			{

				//To read from server all the files shared by it
				read=true;
				while(read)
				{
				String str=(String) ip.readObject();
				//To stop reading when the server stops sending strings
				if(!str.equals("-"))
				p(str);
				else
				read=false;
				}
			}



			p("");
		}

		p("Thanks for using FileSharer --- Programmed by SDS !!");


	}







	private static void recieveFileFromServer(String string, Socket socket) throws IOException {

		int maxSize=1024*5*1024;	//5 MB
		int bytesLeft;
		int bytesRead = 0;
		byte [] fileArray = new byte [maxSize];

		//Copying the file in bytes
		InputStream is = socket.getInputStream();
		FileOutputStream fos = new FileOutputStream(string);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		//storing size of file recieved from server through is into byteLeft
		bytesLeft = is.read(fileArray,0,fileArray.length);

		bytesRead = bytesLeft;	//To know how many bytes to copy at end.
		do
		{
			bytesLeft = is.read(fileArray, bytesRead, (fileArray.length-bytesRead));
		if(bytesLeft >= 0)
			bytesRead += bytesLeft;
		}
		while(bytesLeft > -1);
		bos.write(fileArray, 0 , bytesRead);
		bos.flush();
		bos.close();
		socket.close();
	}






	private static void p(String s) {

		System.out.println(s);

	}


}
