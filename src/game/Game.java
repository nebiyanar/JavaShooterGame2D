package game;

import java.awt.Canvas;
import javax.sound.sampled.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class Game extends Canvas implements Runnable {
    public final static int width = 800;
    public final static int height = 600;
    String title = "After the Apocalypse";
    static protected int killedBoxes = 0;
    public static int killedZombies = 0;
    public static int score = 0;
    public static boolean gameOver = false;
    private int frames = 0;
    private int fps = 0;
    public static Difficulty difficulty = Difficulty.MEDIUM; 
    public static boolean openHealthMenu = false;
    public static Game instance;
    private boolean bonusGiven = false; 
    private TileManager tileManager;
    private BufferedImage menuBackground, startButtonImg, exitButtonImg, musicButtonImg,loadButtonImg;
    Rectangle startButton;
	Rectangle exitButton;
	Rectangle musicButton;
	Rectangle loadButton;
    public boolean musicOn = true;
    public Clip backgroundMusic;
    public boolean showDifficultyMenu = false;
    public Rectangle easyButton = new Rectangle(300, 240, 200, 40);
    public Rectangle mediumButton = new Rectangle(300, 300, 200, 40);
    public Rectangle hardButton = new Rectangle(300, 360, 200, 40);
    public Rectangle testButton = new Rectangle(300, 420, 200, 40); // Yeni test butonu
    public static int spentScore = 0;

    public boolean gameStarted = false;
    public boolean isLoadingGame = false;
    public boolean openEscapeMenu = false;
    public boolean showSaveMenu = false;
    long saveMenuStartTime = 0;
    




    private Thread thread;
    private boolean isRunning = false;
    private KeyInput input;
    private MouseInput minput;
    private Handler handler;
    Camera cam;
    public static  Player player;
    private BufferedImage backgroundImage;
    private boolean isPaused = false;

    private int currentWave = 1;
    private long waveStartTime = System.currentTimeMillis();
    private boolean waveInProgress = false;
    private long waveDelay = 2000;
    private long lastWaveTime = 0;
    private boolean showWaveText = false;
    public boolean openMainMenu = false;
    public Rectangle saveButtonRect = new Rectangle(300, 250, 200, 40);
    public Rectangle loadButtonRect = new Rectangle(300, 310, 200, 40);


    public Game() {
        new Window(width, height, title, this);
        instance=this;
        init();
        start();
    }
    public Handler getHandler() {
        return handler;
    }

    public void startGame() {
        
        gameStarted = true;
        showDifficultyMenu = false;
        openMainMenu = false;
        isPaused = false;
        gameOver = false;
        

        init();
    }

    public void initMusic() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("src/game/backgroundmus.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void init() {
    	if (!isLoadingGame) {
    	    killedZombies = 0;
    	    score = (difficulty == Difficulty.TEST) ? 500 : 0;
    	    currentWave = 1;
    	    bonusGiven = false;
    	}

        isLoadingGame = false;

        if (musicOn && backgroundMusic == null) {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("src/game/backgroundmus.wav"));
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioStream);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        

        try {
            menuBackground = ImageIO.read(new File("src/game/anaekran.png"));
            startButtonImg = ImageIO.read(new File("src/game/startgame.png"));
            exitButtonImg = ImageIO.read(new File("src/game/exit.png"));
            musicButtonImg = ImageIO.read(new File("src/game/music.png"));
            loadButtonImg = ImageIO.read(new File("src/game/loadgame.png"));

            
        } catch (IOException e) {
            e.printStackTrace();
        }

        startButton = new Rectangle(300, 220, 200, 30);
        loadButton  = new Rectangle(300, 270, 200, 30);
        musicButton = new Rectangle(300, 320, 200, 30);
        exitButton  = new Rectangle(300, 370, 200, 30);

       
        
        if (!gameStarted) {
            openMainMenu = true;
        }

        try {
            File file = new File("src/game/bckg2.jpg");
            backgroundImage = ImageIO.read(file);
            System.out.println("Arka plan yüklendi!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        handler = new Handler();
        tileManager = new TileManager();           
        handler.setTileManager(tileManager);        

        input = new KeyInput();

     
     for (var l : this.getKeyListeners()) this.removeKeyListener(l);
     this.addKeyListener(input);

        
        cam = new Camera(0, 0, handler, backgroundImage);
        minput = new MouseInput(handler, cam);

    
     for (var l : this.getMouseListeners()) this.removeMouseListener(l);
     this.addMouseListener(minput);
 
        

        float spawnX = 300, spawnY = 300;
        while (tileManager.hasCollision(new Rectangle((int)spawnX, (int)spawnY, 32, 32))) {
            spawnX += 32; // Bloklarda Sıkışmaması için
        }

        player = new Player(spawnX, spawnY, ID.Player, input, handler, cam);
        Game.player = player;
        input.setPlayer(player);
        handler.addObject(player);
        minput.findPlayer();
        

    }
    private synchronized void start() {
        if (isRunning) return;
        thread = new Thread(this);
        thread.start();
        isRunning = true;
    }

    private synchronized void stop() {
        try {
            if (backgroundMusic != null) backgroundMusic.stop();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isRunning = false;
    }


    public void run() {
    	this.setFocusable(true); 
    	this.requestFocus();     

        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                fps = frames;
                frames = 0;
            }
        }
        stop();
    }

    public void toggleMainMenu() {
        if (showDifficultyMenu) return;
        openMainMenu = !openMainMenu;
        isPaused = openMainMenu;
        
    }



    
    private void tick() {
        if (gameOver) return;
        if (openMainMenu || openEscapeMenu) return;

        if (showSaveMenu && System.currentTimeMillis() - saveMenuStartTime > 2000) {
            showSaveMenu = false; // 2 saniye sonra kapat
        }


        
        if (isPaused || gameOver) return;

        handler.tick();
        cam.tick();
        minput.tick();

        Gun gun = player.getCurrentGun();
        if (gun.isReloading() && System.currentTimeMillis() - gun.reloadStartTime >= gun.reloadDuration) {
            gun.completeReload();
        }

        if (player.getHealth() <= 0) {
            gameOver = true;
            return;
        }

        if (currentWave <= 12 && getRemainingZombies() == 0 && !waveInProgress) {

            
            if (!bonusGiven && currentWave > 1) {
                int bonus = 75+(currentWave - 1) * 50; 
                Game.score += bonus;
                System.out.println((currentWave - 1) + ". dalga tamamlandı! +" + bonus + " skor.");
                bonusGiven = true;
            }

            
            spawnWave(currentWave);

            if (currentWave == 2) player.unlockRifle();
            if (currentWave == 4) player.unlockShotgun();
            if (currentWave == 6) player.unlockSniper();
            if (currentWave == 11) player.unlockRocketLauncher();

            showWaveText = true;
            currentWave++;
            waveInProgress = true;
            waveStartTime = System.currentTimeMillis();
        }


        if (currentWave > 12 && getRemainingZombies() == 0 && !waveInProgress) {
            gameOver = true;
            System.out.println("Tüm Dalgalar Tamamlandı!");
        }

        if (System.currentTimeMillis() - waveStartTime > waveDelay) {
            waveInProgress = false;
            showWaveText = false;
        }
        
      

    }

    private int getRemainingZombies() {
        int count = 0;
        for (GameObject obj : handler.object) {
            if (obj instanceof Zombie) {
                count++;
            }
        }
        return count;
    }

    private void spawnWave(int wave) {
        int totalZombies;
        switch (difficulty) {
            case EASY -> totalZombies = (wave <= 10) ? 2 : 4;
            case MEDIUM -> totalZombies = 3 + wave * 2;
            case HARD -> totalZombies = (int) ((3 + wave * 2) * 1.5);
            case TEST -> totalZombies = (wave < 12) ? 1 : 4;
            default -> totalZombies = 3 + wave * 2;
        }

        for (int i = 0; i < totalZombies; i++) {
            float x = 0, y = 0;
            Rectangle zombieBox;
            int attempts = 0;

            // En fazla 10  deneme yeterli olacaktır daha iyi olsun istersek arttırırız
            do {
                x = (float) (Math.random() * cam.getWorldWidth());
                y = (float) (Math.random() * cam.getWorldHeight());
                zombieBox = new Rectangle((int)x, (int)y, 32, 32);
                attempts++;
            } while (
                (Math.hypot(player.getX() - x, player.getY() - y) < 100 ||
                tileManager.hasCollision(zombieBox) ||
                !tileManager.isSpawnable(zombieBox)) &&
                attempts < 10
            );

            // Uygun değilse kaydır
            if (tileManager.hasCollision(zombieBox)) {
                int[] dx = {0, 32, -32, 0, 0};
                int[] dy = {0, 0, 0, -32, 32};
                for (int j = 0; j < dx.length; j++) {
                    float newX = x + dx[j];
                    float newY = y + dy[j];
                    Rectangle tryRect = new Rectangle((int)newX, (int)newY, 32, 32);
                    if (!tileManager.hasCollision(tryRect)) {
                        x = newX;
                        y = newY;
                        break;
                    }
                }
            }

            Zombie z = (difficulty == Difficulty.TEST && wave == 12)
                ? switch (i) {
                    case 0 -> new NormalZombie(x, y, cam, player);
                    case 1 -> new TankZombie(x, y, cam, player);
                    case 2 -> new CrawlerZombie(x, y, cam, player);
                    case 3 -> new AcidZombie(x, y, cam, player);
                    default -> new NormalZombie(x, y, cam, player);
                }
                : switch (i % 4) {
                    case 0 -> new NormalZombie(x, y, cam, player);
                    case 1 -> new TankZombie(x, y, cam, player);
                    case 2 -> new CrawlerZombie(x, y, cam, player);
                    case 3 -> new AcidZombie(x, y, cam, player);
                    default -> new NormalZombie(x, y, cam, player);
                };

            handler.addObject(z);
        }

        waveInProgress = false;
        lastWaveTime = System.currentTimeMillis();
    }
    
    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        // Giriş Arayüzü
        if (!gameStarted && openMainMenu && !showDifficultyMenu) {
            if (menuBackground != null)
                g.drawImage(menuBackground, 0, 0, Game.width, Game.height, null);
            else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, Game.width, Game.height);
            }

            if (startButtonImg != null)
                g.drawImage(startButtonImg, startButton.x, startButton.y, startButton.width, startButton.height, null);
            if (exitButtonImg != null)
                g.drawImage(exitButtonImg, exitButton.x, exitButton.y, exitButton.width, exitButton.height, null);
            if (musicButtonImg != null)
                g.drawImage(musicButtonImg, musicButton.x, musicButton.y, musicButton.width, musicButton.height, null);
            if (loadButtonImg != null)
                g.drawImage(loadButtonImg, loadButton.x, loadButton.y, loadButton.width, loadButton.height, null);

            g.dispose();
            bs.show();
            return;
        }

        if (showDifficultyMenu) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(100, 100, 600, 400);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("ZORLUK SEÇ", 310, 160);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawRect(easyButton.x, easyButton.y, easyButton.width, easyButton.height);
            g.drawString("Kolay", easyButton.x + 70, easyButton.y + 25);

            g.drawRect(mediumButton.x, mediumButton.y, mediumButton.width, mediumButton.height);
            g.drawString("Orta", mediumButton.x + 75, mediumButton.y + 25);

            g.drawRect(hardButton.x, hardButton.y, hardButton.width, hardButton.height);
            g.drawString("Zor", hardButton.x + 80, hardButton.y + 25);

            
            g.drawRect(testButton.x, testButton.y, testButton.width, testButton.height);
            g.drawString("TEST", testButton.x + 75, testButton.y + 25);

            g.dispose();
            bs.show();
            return;
        }


        // Oyun
        g2d.translate(-cam.getX(), -cam.getY());
        if (tileManager != null) tileManager.render(g2d);
        handler.render(g2d);
        g2d.translate(cam.getX(), cam.getY());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String controlsText = "H: Health Menu  |  M: Save";
        int texttWidth = g.getFontMetrics().stringWidth(controlsText);
        g.drawString(controlsText, (Game.width - texttWidth) / 2 + 20, 20);
        
        
        String musicText = "F: Music ON/OFF";
        int musicTextWidth = g.getFontMetrics().stringWidth(musicText);
        g.drawString(musicText, (Game.width - musicTextWidth) / 2 + 20, 40);
        String bilText = "NEBİ YANAR 231301037";
        int bilTextWidth = g.getFontMetrics().stringWidth(bilText);
        g.drawString(bilText, (Game.width - bilTextWidth) / 2 + 20, 55);


        
   

        
        
        // Üst Yazılar
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Killed Zombies: " + killedZombies, Game.width - 190, 20);
        int netScore = score - spentScore;
        g.drawString("Score: " + netScore + "/" + score, Game.width - 190, 40);


        Gun gun = player.getCurrentGun();
        g.drawString("Weapon: " + gun.getClass().getSimpleName(), 15, 20);
        g.drawString("Ammo: " + gun.getCurrentAmmo() + "/" + gun.getReserveAmmo(), 15, 40);

        g.setColor(Color.RED);
        g.fillRect(10, 50, player.getHealth() * 2, 20);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Health: " + player.getHealth(), 15, 65);

        if (gun.isReloading()) {
            int radius = 12;

            int centerX = (int) (player.getX() - cam.getX() + 16);
            int centerY = (int) (player.getY() - cam.getY() + 16);

            g.setColor(Color.WHITE);
            g.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            float progress = gun.getReloadProgress();

            //Geçiş Algoritması(R -> G)
            int red = (int) (255 * (1 - progress));
            int green = (int) (255 * progress);
            g.setColor(new Color(red, green, 0));

            g.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 90, -(int) (360 * progress));
        }



        if (showWaveText && currentWave > 1 && currentWave <= 13) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setColor(Color.YELLOW);
            String text = (currentWave - 1) + ". Dalga";
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, Game.width / 2 - textWidth / 2, Game.height / 2 - 100);
        }

        // Heal Menu
        if (openHealthMenu) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(100, 100, 600, 400);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("SAĞLIK MENÜSÜ", 310, 140);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("[K] 100 Skor → +20 Can", 240, 200);
            g.drawString("[L] 250 Skor → +20 Max Can && Anlık +20 Regen ", 240, 240);
            g.drawString("[U] 150 Skor → Full Can (regen)", 240, 280);
            g.drawString("[O] Menüden Çık", 240, 320);
        }

        //  M ile Save 
        if (showSaveMenu) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(250, 200, 300, 150);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Oyun Kaydedildi!", 290, 260);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Save Alindi", 270, 300);
        }

        // Oyunun iki türlü bitmesi
        if (gameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            if (player.getHealth() <= 0)
                g.drawString("YOU DIED!", Game.width / 2 - 130, Game.height / 2);
            else
                g.drawString("TÜM DALGALAR TAMAMLANDI!", Game.width / 2 - 390, Game.height / 2);
        }

        // FPS
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("FPS: " + fps, Game.width - 100, 70);

        g.dispose();
        bs.show();
    }

    public void togglePause() {
        isPaused = !isPaused;
        System.out.println("Pause durumu: " + (isPaused ? "Durduruldu" : "Devam Ediyor"));
    }

    public void saveGame() {
        try {
            FileWriter writer = new FileWriter("savegame.txt");

            // Dosyaya zombi player ve gun infoları yaz tekrar okuyacağız
            writer.write("PLAYER," +
            	    player.getX() + "," +
            	    player.getY() + "," +
            	    player.getHealth() + "," +
            	    player.getMaxHealth() + "," +
            	    killedZombies + "," +
            	    score + "," +
            	    spentScore + "," + 
            	    currentWave + "," +
            	    (player.rifle != null) + "," +
            	    (player.shotgun != null) + "," +
            	    (player.sniper != null) + "," +
            	    (player.rocketLauncher != null) + "," +
            	    (player.rifle != null ? player.rifle.getCurrentAmmo() : -1) + "," +
            	    (player.rifle != null ? player.rifle.getReserveAmmo() : -1) + "," +
            	    (player.shotgun != null ? player.shotgun.getCurrentAmmo() : -1) + "," +
            	    (player.shotgun != null ? player.shotgun.getReserveAmmo() : -1) + "," +
            	    (player.sniper != null ? player.sniper.getCurrentAmmo() : -1) + "," +
            	    (player.sniper != null ? player.sniper.getReserveAmmo() : -1) + "," +
            	    (player.rocketLauncher != null ? player.rocketLauncher.getCurrentAmmo() : -1) + "," +
            	    (player.rocketLauncher != null ? player.rocketLauncher.getReserveAmmo() : -1) + "," +
            	    player.pistol.getCurrentAmmo() + "," +
            	    player.pistol.getReserveAmmo() + "," +
            	    Game.difficulty.name() + "\n");




            
            for (GameObject obj : handler.object) {
                if (obj instanceof Zombie zombie) {
                    writer.write("ZOMBIE," + zombie.getClass().getSimpleName() + "," + zombie.getX() + "," + zombie.getY() + "," + zombie.health + "\n");
                }
            }

            writer.close();
            System.out.println("Oyun başarıyla kaydedildi!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadGame() {
        isLoadingGame = true;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("savegame.txt"));
            String line;

            
            handler = new Handler();
            tileManager = new TileManager();
            handler.setTileManager(tileManager);
            input = new KeyInput();

         
         for (var l : this.getKeyListeners()) this.removeKeyListener(l);
         this.addKeyListener(input);
            cam = new Camera(0, 0, handler, backgroundImage);
            minput = new MouseInput(handler, cam);
            this.minput = minput;

            
            for (var l : this.getMouseListeners()) this.removeMouseListener(l);
            this.addMouseListener(minput);
            minput.findPlayer(); 
            player = new Player(0, 0, ID.Player, input, handler, cam); 
            Game.player = player;
            input.setPlayer(player);
            minput.findPlayer();
            handler.addObject(player);

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals("PLAYER")) {
                    float savedX = Float.parseFloat(data[1]);
                    float savedY = Float.parseFloat(data[2]);
                    int savedHealth = Integer.parseInt(data[3]);
                    int savedMaxHealth = Integer.parseInt(data[4]);
                    killedZombies = Integer.parseInt(data[5]);
                    if (data.length >= 24) { //zorluga gelcek 
                        try {
                            Game.difficulty = Difficulty.valueOf(data[23]);
                            System.out.println("Yüklenen zorluk seviyesi: " + Game.difficulty);
                        } catch (Exception ex) {
                            System.out.println("Zorluk seviyesi okunamadı, varsayılan MEDIUM kullanılacak.");
                            Game.difficulty = Difficulty.MEDIUM;
                        }
                    }

                    score = Integer.parseInt(data[6]);
                    spentScore = (data.length >= 24) ? Integer.parseInt(data[7]) : 0; 
                    currentWave = Integer.parseInt(data[8]);

                  
                    
                    player.setX(savedX);
                    player.setY(savedY);

                    Rectangle checkRect = new Rectangle((int) savedX, (int) savedY, 32, 32);
                    TileManager tileManager = handler.getTileManager();

                    // Stuck kalcak şekilde doğduysa kaydır
                    if (tileManager != null && tileManager.hasCollision(checkRect)) {
                        

                        boolean found = false;
                        int[] dx = {0, 32, -32, 0, 0}; // sağ, sol, yukarı, aşağı
                        int[] dy = {0, 0, 0, -32, 32};

                        for (int i = 0; i < dx.length; i++) {
                            float newX = savedX + dx[i];
                            float newY = savedY + dy[i];
                            Rectangle tryRect = new Rectangle((int) newX, (int) newY, 32, 32);

                            if (!tileManager.hasCollision(tryRect)) {
                                player.setX(newX);
                                player.setY(newY);
                                found = true;
                                break;
                            }
                        }

                       
                    }

                    
                    
                    
                    player.setMaxHealth(savedMaxHealth);
                    player.takeDamage(player.getHealth() - savedHealth);
                    player.pistol.setCurrentAmmo(Integer.parseInt(data[21]));
                    player.pistol.setReserveAmmo(Integer.parseInt(data[22]));

                    // Silahlar
                    boolean hasRifle = Boolean.parseBoolean(data[9]);
                    boolean hasShotgun = Boolean.parseBoolean(data[10]);
                    boolean hasSniper = Boolean.parseBoolean(data[11]);
                    boolean hasRocket = Boolean.parseBoolean(data[12]);

                    if (hasRifle) {
                        player.unlockRifle();
                        player.rifle.setCurrentAmmo(Integer.parseInt(data[13]));
                        player.rifle.setReserveAmmo(Integer.parseInt(data[14]));
                    }

                    if (hasShotgun) {
                        player.unlockShotgun();
                        player.shotgun.setCurrentAmmo(Integer.parseInt(data[15]));
                        player.shotgun.setReserveAmmo(Integer.parseInt(data[16]));
                    }

                    if (hasSniper) {
                        player.unlockSniper();
                        player.sniper.setCurrentAmmo(Integer.parseInt(data[17]));
                        player.sniper.setReserveAmmo(Integer.parseInt(data[18]));
                    }

                    if (hasRocket) {
                        player.unlockRocketLauncher();
                        player.rocketLauncher.setCurrentAmmo(Integer.parseInt(data[19]));
                        player.rocketLauncher.setReserveAmmo(Integer.parseInt(data[20]));
                    }

                } else if (data[0].equals("ZOMBIE")) {
                    String type = data[1];
                    float x = Float.parseFloat(data[2]);
                    float y = Float.parseFloat(data[3]);
                    int health = Integer.parseInt(data[4]);

                    Rectangle zombieRect = new Rectangle((int)x, (int)y, 32, 32);
                    TileManager tileManager = handler.getTileManager();

                    // 10 kez dene daha iyi olsun istersek arttırırız
                    int attempts = 0;
                    while ((tileManager.hasCollision(zombieRect) || !tileManager.isSpawnable(zombieRect)) && attempts < 10) {
                        x = (float) (Math.random() * cam.getWorldWidth());
                        y = (float) (Math.random() * cam.getWorldHeight());
                        zombieRect = new Rectangle((int)x, (int)y, 32, 32);
                        attempts++;
                    }

                    // Yukardaki Playerin aynisi
                    if (tileManager.hasCollision(zombieRect)) {
                        int[] dx = {0, 32, -32, 0, 0};
                        int[] dy = {0, 0, 0, -32, 32};
                        for (int i = 0; i < dx.length; i++) {
                            float newX = x + dx[i];
                            float newY = y + dy[i];
                            Rectangle tryRect = new Rectangle((int)newX, (int)newY, 32, 32);
                            if (!tileManager.hasCollision(tryRect)) {
                                x = newX;
                                y = newY;
                                break;
                            }
                        }
                    }

                    Zombie z = switch (type) {
                        case "NormalZombie" -> new NormalZombie(x, y, cam, player);
                        case "TankZombie" -> new TankZombie(x, y, cam, player);
                        case "CrawlerZombie" -> new CrawlerZombie(x, y, cam, player);
                        case "AcidZombie" -> new AcidZombie(x, y, cam, player);
                        default -> null;
                    };

                    if (z != null) {
                        z.health = health;
                        handler.addObject(z);
                    }
                }

            }

            reader.close();
            gameStarted = true;
            openMainMenu = false;
            showDifficultyMenu = false;
            isPaused = false;
            System.out.println("Kayıtlı oyun yüklendi!");
        } catch (IOException e) {
            System.out.println("Kayıtlı oyun bulunamadı!");
        }
        minput.findPlayer(); 

    }



    public static void main(String[] args) {
        new Game();
    }
}