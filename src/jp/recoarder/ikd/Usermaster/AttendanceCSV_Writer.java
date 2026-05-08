package jp.recoarder.ikd.Usermaster;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import jp.recoarder.ikd.assets.Sound;

public class AttendanceCSV_Writer {

    private static final String ROOT_PATH = "Lab_TimeRecoader/output";

    public static void mkdirs() {
        new File(ROOT_PATH).mkdirs();

        for (String id : UserMaster.getAllUserIDs()) {
            new File(ROOT_PATH + "/" + id).mkdirs();
        }
    }

    public static void write(LocalDate ld) {
        mkdirs();

        for (String id : UserMaster.getAllUserIDs()) {

            LocalDate start = ld.withDayOfMonth(1);
            LocalDate end = ld.withDayOfMonth(ld.lengthOfMonth());

            File f = new File(ROOT_PATH + "/" + id + "/"
                    + ld.getYear() + "-" + ld.getMonthValue() + ".csv");

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, false))) {

                bw.write("Date"+","+"Start" + "," + "End" + ",");
                bw.newLine();
                for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

                    LocalTime startTimel = AttendanceReader.getTime(id, true, date);
                    LocalTime endTimel = AttendanceReader.getTime(id, false, date);

                    String startTime = "";
                    String endTime = "";
                    String diffTime = "";

                    if (startTimel != null)
                        startTime = String.format("%02d:%02d", startTimel.getHour(), startTimel.getMinute());

                    if (endTimel != null)
                        endTime = String.format("%02d:%02d", endTimel.getHour(), endTimel.getMinute());

                    if (startTimel != null && endTimel != null) {
                        Duration diffTimel = Duration.between(startTimel, endTimel);

                        long hours = diffTimel.toHours();
                        long minutes = diffTimel.toMinutes() % 60;

                        diffTime = String.format("%02d:%02d", hours, minutes);
                    }

                    // 1行書き込み
                    bw.write(date.getMonthValue()+"/"+date.getDayOfMonth()+","+startTime + "," + endTime + "," + diffTime);
                    bw.newLine();
                }

            } catch (IOException e) {
                Sound.CritricalError();
                e.printStackTrace();
            }
        }
    }
}