import java.util.Random;
import java.util.Scanner;

class Hero {
    String name;
    int agility;
    int health;
    int experience;
    int gold;
    int strength;

    public Hero(String name, int agility, int health, int experience, int gold, int strength) {
        this.name = name;
        this.agility = agility;
        this.health = health;
        this.experience = experience;
        this.gold = gold;
        this.strength = strength;
    }

    public int attack() {
        if (Math.random() < 0.1) {  // 10% chance of a critical hit
            return strength * 2;
        }
        return strength;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public void gainExperience(int experience) {
        this.experience += experience;
    }

    public void buyPotion() {
        if (gold >= 5) {
            gold -= 5;
            health += 20;
            System.out.println(name + " bought a health potion. Current health: " + health);
        } else {
            System.out.println("Not enough gold to buy a health potion.");
        }
    }
}

class Goblin {
    int health;
    int strength;

    public Goblin(int health, int strength) {
        this.health = health;
        this.strength = strength;
    }

    public int attack() {
        return strength;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }
}

class Skeleton {
    int health;
    int strength;

    public Skeleton(int health, int strength) {
        this.health = health;
        this.strength = strength;
    }

    public int attack() {
        return strength;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }
}

class Merchant {
    int[] inventory = {5};  // Initial number of health potions

    public void sellPotion(Hero hero) {
        if (inventory.length > 0 && hero.gold >= 5) {
            hero.gold -= 5;
            hero.health += 20;
            inventory[0] -= 1;
            System.out.println(hero.name + " bought a health potion. Current health: " + hero.health);
        } else if (hero.gold < 5) {
            System.out.println("Not enough gold to buy a health potion.");
        } else {
            System.out.println("Out of stock for health potions.");
        }
    }
}

class Battle {
    Hero hero;
    Object enemy;
    boolean isBattleOver = false;

    public Battle(Hero hero, Object enemy) {
        this.hero = hero;
        this.enemy = enemy;
    }

    public void attackTurn(Object attacker, Object target) {
        if (attacker == hero) {
            System.out.println(hero.name + " attacks " + target.getClass().getSimpleName() + "!");
        } else {
            System.out.println(target.getClass().getSimpleName() + " attacks " + hero.name + "!");
        }

        if (calculateHit(((Hero) attacker).agility)) {
            int damage = ((Hero) attacker).attack();
            if (target instanceof Goblin) {
                ((Goblin) target).takeDamage(damage);
            } else if (target instanceof Skeleton) {
                ((Skeleton) target).takeDamage(damage);
            }

            System.out.println(target.getClass().getSimpleName() + " takes " + damage + " damage.");

            if (target instanceof Goblin && ((Goblin) target).health == 0) {
                endBattle(attacker);
            } else if (target instanceof Skeleton && ((Skeleton) target).health == 0) {
                endBattle(attacker);
            }
        } else {
            System.out.println("Attack missed!");
        }
    }

    public boolean calculateHit(int agility) {
        return agility * 3 > new Random().nextInt(100);
    }

    public void endBattle(Object winner) {
    if (winner == hero) {
        System.out.println(hero.name + " wins the battle!");
        hero.gainExperience(10);
        hero.gold += 5;
    } else {
        System.out.println(((Hero) winner).name + " defeats " + hero.name + ". Game over.");
    }

    isBattleOver = true;
}



    public void startBattle() {
        System.out.println("Battle begins!");

        while (!isBattleOver) {
            try {
                Thread.sleep(1000);  // Simulate time passing
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (new Random().nextBoolean()) {  // Randomly determine who attacks
                attackTurn(hero, enemy);
            } else {
                attackTurn(enemy, hero);
            }
        }
    }
}

class Game {
    Hero player;
    String currentLocation = "city";
    Merchant merchant = new Merchant();
    Object currentEnemy = null;
    boolean isInBattle = false;

    public void startGame() {
        System.out.println("Welcome to the Adventure Game!");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your hero's name: ");
        String playerName = scanner.nextLine();
        player = new Hero(playerName, 5, 100, 0, 10, 5);

        while (true) {
            if ("city".equals(currentLocation)) {
                showCityOptions();
            } else if ("forest".equals(currentLocation)) {
                enterForest();
            }
        }
    }

    public void showCityOptions() {
        System.out.println("\nOptions:");
        System.out.println("1. Go to the merchant");
        System.out.println("2. Enter the dark forest");
        System.out.println("3. Exit");

        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();

        if ("1".equals(choice)) {
            visitMerchant();
        } else if ("2".equals(choice)) {
            enterForest();
        } else if ("3".equals(choice)) {
            System.out.println("Goodbye!");
            System.exit(0);
        } else {
            System.out.println("Invalid choice. Please enter a number from the options.");
        }
    }

    public void visitMerchant() {
        if (merchant.inventory.length > 0) {
            System.out.println("Welcome to the merchant's shop!");
            System.out.println("Options:");
            System.out.println("1. Buy a health potion");
            System.out.println("2. Return to the city");

            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine();

            if ("1".equals(choice)) {
                merchant.sellPotion(player);
            } else if ("2".equals(choice)) {
                currentLocation = "city";
            } else {
                System.out.println("Invalid choice. Please enter a number from the options.");
            }
        } else {
            System.out.println("The merchant is not working right now. Come back later.");
        }
    }

    public void enterForest() {
        System.out.println("You entered the dark forest.");

        if (!isInBattle) {
            spawnRandomEnemy();
        }

        Battle battle = new Battle(player, currentEnemy);
        battle.startBattle();

        if (player.health > 0) {
            afterBattleOptions();
        }
    }

    public void spawnRandomEnemy() {
        if (new Random().nextBoolean()) {
            currentEnemy = new Goblin(30, 5);
        } else {
            currentEnemy = new Skeleton(20, 7);
        }

        System.out.println("A wild " + currentEnemy.getClass().getSimpleName() + " appears!");
    }

    public void afterBattleOptions() {
        System.out.println("\nOptions:");
        System.out.println("1. Return to the city");
        System.out.println("2. Continue exploring the forest");

        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();

        if ("1".equals(choice)) {
            currentLocation = "city";
            isInBattle = false;
        } else if ("2".equals(choice)) {
            isInBattle = false;
            enterForest();
        } else {
            System.out.println("Invalid choice. Please enter a number from the options.");
        }
    }
}

    public class Game {
    public static void main(String[] args) {
        Game game = new Game();
        game.startGame();
    }
}
