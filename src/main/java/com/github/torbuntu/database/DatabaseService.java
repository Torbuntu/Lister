/*
 * Copyright 2020 tor.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.torbuntu.database;

import com.github.torbuntu.list.ListContainer;
import com.github.torbuntu.list.ListEntry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tor
 */
public class DatabaseService {

    public static void insertEntry(String title) {
        String sql = "INSERT INTO LISTS (TITLE) VALUES(?)";

        try ( Connection c = getConnection();  PreparedStatement s = c.prepareStatement(sql)) {

            s.setString(1, title);
            s.executeUpdate();

            Logger.getLogger(DatabaseService.class.getName()).log(Level.INFO, "Items save successfully");
        } catch (Exception e) {
            Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, "Failed to save to DB. Cause: ", e);
        }
    }

    public static void deleteEntry(String title) {
        String sql = "DELETE FROM LISTS WHERE TITLE = ?";
        try ( Connection c = getConnection();  PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, title);
            s.execute();

        } catch (Exception e) {
            Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, "Failed to retrieve Lists. Cause: ", e);
        }
    }

    public static ListContainer selectEntries() {
        String sql = "SELECT * FROM LISTS";
        ListContainer lc = new ListContainer(new ArrayList<>());
        try ( Connection c = getConnection();  Statement s = c.createStatement();  ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                lc.addEntry(new ListEntry(r.getInt("LIST_ID"), r.getString("TITLE")));
            }
            return lc;
        } catch (Exception e) {
            Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, "Failed to retrieve Lists. Cause: ", e);
            return lc;
        }
    }

    public static void insertUpdateListEntries(ListContainer lc) {
        String sql = "INSERT OR REPLACE INTO LISTS (LIST_ID, TITLE) VALUES(?, ?)";

        try ( Connection c = getConnection();  PreparedStatement s = c.prepareStatement(sql)) {

            for (ListEntry le : lc.getEntries()) {
                s.setInt(1, le.getId());
                s.setString(2, le.getTitle());
                s.addBatch();
            }

            int[] count = s.executeBatch();

            Logger.getLogger(DatabaseService.class.getName()).log(Level.INFO, String.format("{%d} Items saved successfully", count.length));
        } catch (Exception e) {
            Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, "Failed to save to DB. Cause: ", e);
        }
    }

    public static boolean ensureListsTableExists() {
        try ( Connection c = getConnection();  Statement s = c.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS LISTS (\n" + "LIST_ID INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,\n" + "TITLE           TEXT  NOT NULL\n" + ")\n";
            s.executeUpdate(sql);
            Logger.getLogger(DatabaseService.class.getName()).log(Level.INFO, "DB Check Succeeded");
            return true;
        } catch (Exception e) {
            Logger.getLogger(DatabaseService.class.getName()).log(Level.WARNING, "Failed to ensure db created. Items will not be saved to database. Cause: ", e);
            return false;
        }
    }

    public static boolean ensureItemsTableExists() {
        try ( Connection c = getConnection();  Statement s = c.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS ITEMS (\n"
                    + "ITEM_ID      INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,\n"
                    + "TEXT_DATA    TEXT  NOT NULL,\n"
                    + "ENTRY_ID     INTEGER NOT NULL\n"
                    + ")\n";
            s.executeUpdate(sql);
            Logger.getLogger(DatabaseService.class.getName()).log(Level.INFO, "DB Check Succeeded");
            return true;
        } catch (Exception e) {
            Logger.getLogger(DatabaseService.class.getName()).log(Level.WARNING, "Failed to ensure db created. Items will not be saved to database. Cause: ", e);
            return false;
        }
    }

    private static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:lister.db");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to connect to DB");
        }
    }

}
