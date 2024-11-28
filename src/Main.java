import GUI.MainPage;

public class Main extends MainPage {

    public static void main(String[] args) {
        System.out.println("Running....");
        MainPage mainPage = new MainPage();
        mainPage.navigator("/loginPage");
    }
}