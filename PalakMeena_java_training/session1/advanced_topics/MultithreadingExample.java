package PalakMeena_java_training.session1.advanced_topics;

//Example of Multithreading in Java to simulate a social media feed loading while the UI is rendering

class FeedLoader extends Thread {
    public void run() {
        System.out.println("Fetching latest posts from server...");
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
        } // Simulates slow network
        System.out.println("✅ Feed Loaded!");
    }
}

class UIRenderer extends Thread {
    public void run() {
        System.out.println("Rendering buttons and profile icons...");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        System.out.println("✨ UI is ready and interactive!");
    }
}

public class MultithreadingExample {
    public static void main(String[] args) {
        FeedLoader networkThread = new FeedLoader();
        UIRenderer uiThread = new UIRenderer();

        // If we didn't use threads, the UI would wait 3 seconds for the feed.
        // With threads, they start together!
        networkThread.start();
        uiThread.start();
    }
}