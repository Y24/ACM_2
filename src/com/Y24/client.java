package com.Y24;

//test data 1 10 12 0 1 1 2 2 3 3 4 4 5 5 6  6 7 7 8 8 9 0 9 0 2 2 5 3 0 0 1 1 5 1

/*
  1 20 23 0 1 0 2 1 2 2 3 2 16 3 4 4 5 5 6 6 7 7 8 8 9 8 10 10 11 12 11 12 8 12 19 19 14 13
 14 15 14 19 16 16 17 17 18 3 18 12 0 1 1 0 3 1 5 0 7 1 9 1 10 0 12 0 14 1 15 1 16 0 18 0
*/
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

enum color {
    WHITE, BLACK, RED
}

class city {
    private ArrayList<Integer> boundary;
    private int id;
    private color flag;

    city(int id) {
        this.id = id;
        this.flag = color.WHITE;
        this.boundary = new ArrayList<Integer>();
    }

    color getFlag() {
        return flag;
    }

    void setFlag(color flag) {
        this.flag = flag;
    }

    int getId() {
        return id;
    }

    void setBoundary(int id) {
        this.boundary.add(id);
    }

    final ArrayList<Integer> getBoundary() {
        return boundary;
    }
}

class cities {
    private ArrayList<city> inter;

    cities(int size, final int[][] roads, final int[][] belongs, final int roadSum, final int belongSum) {
        inter = new ArrayList<city>();
        for (int i = 0; i < size; i++) {
            inter.add(new city(i));
        }
        for (int i = 0; i < belongSum; i++)
            inter.get(belongs[i][0]).setFlag(belongs[i][1] == 1 ? color.BLACK : color.RED);
        for (int i = 0; i < roadSum; i++) {
            inter.get(roads[i][0]).setBoundary(roads[i][1]);
            inter.get(roads[i][1]).setBoundary(roads[i][0]);
        }
    }

    int size() {
        return inter.size();
    }

    void setColor(int id, color flag) throws Exception {
        for (city c : inter)
            if (c.getId() == id) {
                c.setFlag(flag);
                return;
            }

        throw new Exception("At global cities we cannot find city by id:" + id);
    }

    city getCity(int id) throws Exception {
        for (city c : inter
        )
            if (c.getId() == id) return c;
        throw new Exception("At global cities we cannot find city by id:" + id);
    }
}

class kingdom {
    private boolean belong;
    private ArrayList<Integer> inter;
    private ArrayList<Integer> boundary;
    private ArrayList<Integer> added_flag;

    kingdom(city c, final cities global) {
        this.belong = c.getFlag() == color.BLACK;
        this.inter = new ArrayList<Integer>();
        this.boundary = new ArrayList<Integer>();
        added_flag = new ArrayList<Integer>();
        this.inter.add(c.getId());
        try {
            boundary.addAll(global.getCity(c.getId()).getBoundary());
        } catch (Exception e) {
            System.out.print(e.toString());
        }
    }

    private boolean contain(int id, final ArrayList<Integer> all) {
        for (int j : all)
            if (j == id) return true;
        return false;
    }

    boolean boundaryAnalyze(final cities global) throws Exception {
        added_flag.clear();
        for (int i : boundary
        )
            if (global.getCity(i).getFlag() == (belong ? color.BLACK : color.RED)) {
                if (!contain(i, added_flag)) added_flag.add(i);
            }
        return !added_flag.isEmpty();
    }

    private boolean isIn(int id) {
        if (contain(id, inter))
            return true;
        return contain(id, boundary);
    }

    private void boundaryUnion(city c) throws Exception {
        for (int i : c.getBoundary())
            if (!isIn(i))
                boundary.add(i);
    }

    void increase(final cities global) throws Exception {
        for (int i : added_flag) {
            inter.add(i);
            city c = global.getCity(i);
            ArrayList<Integer> boundaryBak = clone(boundary);
            for (Integer j : boundaryBak)
                if (j == i)
                    boundary.remove(j);
            boundaryUnion(c);
        }
    }

    private ArrayList<Integer> clone(ArrayList<Integer> boundary) {
        return new ArrayList<Integer>(boundary);
    }

    ArrayList<Integer> getInter() {
        return inter;
    }

    ArrayList<Integer> getBoundary() {
        return boundary;
    }

    boolean getBelong() {
        return belong;
    }

    int size() {
        return inter.size();
    }
}

class kingdoms {
    private ArrayList<kingdom> inter;

    kingdoms() {
        inter = new ArrayList<kingdom>();
    }

    void addNew(kingdom k) {
        inter.add(k);
    }

    void clear() {
        inter.clear();
    }

    int size() {
        int count = 0;
        for (kingdom k : inter)
            count += k.size();
        return count;
    }

    boolean getBelong() {
        return inter.get(0).getBelong();
    }

    ArrayList<kingdom> getInter() {
        return inter;
    }
}

class competitor {
    private ArrayList<Integer> redSizes;
    private ArrayList<Integer> blackSizes;

    competitor() {
        redSizes = new ArrayList<Integer>();
        blackSizes = new ArrayList<Integer>();
    }

    ArrayList<Integer> getBlackSizes() {
        return blackSizes;
    }

    ArrayList<Integer> getRedSizes() {
        return redSizes;
    }

}

class Game {
    private cities global;
    private ArrayList<Integer> changedCities;
    private HashMap<Integer, competitor> whiteCities;
    private kingdoms bK;
    private kingdoms rK;
    private color winner;
    private int[] end;
    private String result;

    Game(cities global) {
        this.global = global;
        changedCities = new ArrayList<Integer>();
        whiteCities = new HashMap<Integer, competitor>();
        bK = new kingdoms();
        rK = new kingdoms();
        winner = color.WHITE;
        end = new int[2];
        result = "";
        try {
            formKingdom();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void formKingdom() throws Exception {
        boolean[] processed = new boolean[global.size()];
        this.bK.clear();
        this.rK.clear();
        for (int i = 0; i < global.size(); i++)
            processed[i] = global.getCity(i).getFlag() == color.WHITE;

        for (int i = 0; i < global.size(); i++) {
            city c = global.getCity(i);
            if (!processed[i]) {
                kingdom k = new kingdom(c, this.global);
                while (k.boundaryAnalyze(this.global))
                    k.increase(this.global);
                for (int j : k.getInter())
                    processed[j] = true;
                if (k.getBelong())
                    this.bK.addNew(k);
                else this.rK.addNew(k);
            }
        }
    }

    void firstStage() throws Exception {
        while (whiteAnalyze()) {
            invadePerSecond();
            try {
                formKingdom();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void invadePerSecond() throws Exception {
        for (Map.Entry<Integer, competitor> entry : whiteCities.entrySet())
            global.setColor(entry.getKey(), judge(entry.getValue()));
    }

    private color judge(final competitor value) {
        int bmax = Max(value.getBlackSizes());
        int rmax = Max(value.getRedSizes());
        if (bmax > rmax) return color.BLACK;
        else if (bmax < rmax) return color.RED;
        int bsum = Sum(value.getBlackSizes());
        int rsum = Sum(value.getRedSizes());
        if (bsum > rsum) return color.BLACK;
        else if (bsum < rsum)
            return color.RED;
        int btsum = bK.size();
        int rtsum = rK.size();
        if (btsum > rtsum) return color.BLACK;
        else if (btsum < rtsum) return color.RED;
        else return color.BLACK;
    }

    private int Sum(final ArrayList<Integer> Sizes) {
        int sum = 0;
        for (int i : Sizes)
            sum += i;
        return sum;
    }

    private int Max(final ArrayList<Integer> Sizes) {
        int max = 0;
        for (int i : Sizes)
            if (max < i)
                max = i;
        return max;
    }

    private boolean whiteAnalyze() throws Exception {
        whiteCities.clear();
        whiteAnalyzeSingle(bK);
        whiteAnalyzeSingle(rK);
        return !whiteCities.isEmpty();
    }

    private void whiteAnalyzeSingle(final kingdoms ks) throws Exception {
        for (kingdom k : ks.getInter())
            for (Integer id : k.getBoundary())
                if (global.getCity(id).getFlag() == color.WHITE) {
                    if (!containsKey(whiteCities, id))
                        whiteCities.put(id, new competitor());
                    if (ks.getBelong())
                        whiteCities.get(id).getBlackSizes().add(k.size());
                    else whiteCities.get(id).getRedSizes().add(k.size());

                }


    }

    private boolean containsKey(HashMap<Integer, competitor> whiteCities, Integer id) {
        for (int i : whiteCities.keySet())
            if (i == id)
                return true;
        return false;
    }

    String SecondStage() throws Exception {
        while (gameAnalyze()) {
            attackPerSecond();
            try {
                formKingdom();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bK.size() == 0)
            winner = color.RED;
        else if (rK.size() == 0)
            winner = color.BLACK;
        else {
            winner = color.WHITE;
            end = new int[]{rK.size(), bK.size()};
        }
        return result_record();
    }

    private void attackPerSecond() throws Exception {
        ArrayList<Integer> flag = new ArrayList<Integer>();
        for (int id : changedCities)
            if (!contains(id, flag)) {
                global.setColor(id, global.getCity(id).getFlag() == color.BLACK ? color.RED : color.BLACK);
                flag.add(id);
            }
    }

    private boolean contains(int id, ArrayList<Integer> flag) {
        for (int i : flag)
            if (i == id)
                return true;
        return false;
    }

    private boolean gameAnalyze() throws Exception {
        changedCities.clear();
        gameAnalyzeSingle(bK);
        gameAnalyzeSingle(rK);
        return !changedCities.isEmpty();
    }

    private void gameAnalyzeSingle(kingdoms Ks) throws Exception {
        for (kingdom k : Ks.getInter()) {
            ArrayList<Integer> boundary = k.getBoundary();
            for (int i : boundary) {
                kingdom kingdom = findKingdom(i);
                if (kingdom.size() < k.size())
                    changedCities.addAll(kingdom.getInter());
            }
        }
    }

    private kingdom findKingdom(int id) throws Exception {
        for (kingdom kingdom : bK.getInter())
            for (int i : kingdom.getInter())
                if (i == id) return kingdom;
        for (kingdom kingdom : rK.getInter())
            for (int i : kingdom.getInter())
                if (i == id) return kingdom;
        throw new Exception("cannot find kingdom by id:" + id);
    }

    private String result_record() {
        switch (winner) {
            case BLACK:
                result = "Black";
                break;
            case RED:
                result = "Red";
                break;
            case WHITE:
                result = end[0] + " " + end[1];
                break;
        }
        return result;
    }
}

public class client {

    public static void main(String[] args) {
        // write your code here
        int test_sum;
        Scanner input = new Scanner(System.in);
        test_sum = input.nextInt();
        String[] result = new String[test_sum];
        for (int i = 0; i < test_sum; i++) {
            int size, roads_sum, belongs_sum;
            int[][] roads;
            int[][] belongs;
            size = input.nextInt();
            roads_sum = input.nextInt();
            roads = new int[roads_sum][2];
            for (int j = 0; j < roads_sum; j++) {
                roads[j][0] = input.nextInt();
                roads[j][1] = input.nextInt();
            }
            belongs_sum = input.nextInt();
            belongs = new int[belongs_sum][2];
            for (int j = 0; j < belongs_sum; j++) {
                belongs[j][0] = input.nextInt();
                belongs[j][1] = input.nextInt();
            }
            cities global = new cities(size, roads, belongs, roads_sum, belongs_sum);
            Game game = new Game(global);
            try {
                game.firstStage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                result[i] = game.SecondStage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        resultOutput(result);
    }

    private static void resultOutput(String[] result) {
        for (int i = 0; i < result.length; i++)
            System.out.println("Case #" + (i + 1) + ": " + result[i]);
    }
}
