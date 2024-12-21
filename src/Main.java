import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Administrator admin = Administrator.getInstance();
        Thread adminThread = new Thread(admin);
        adminThread.start();
        while (true) {
            Scanner input = new Scanner(System.in);
            input = new Scanner(System.in);
            System.out.println("Введите этаж на который вызван лифт");
            int peopleFloor = input.nextInt();
            System.out.println("Введите куда нужно отправить лифт");
            int targetFloor = input.nextInt();
            admin.selectElevator(peopleFloor, targetFloor);
        }

    }
}
