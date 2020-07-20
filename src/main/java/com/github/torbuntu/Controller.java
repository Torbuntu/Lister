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
package com.github.torbuntu;

import com.github.torbuntu.database.DatabaseService;
import com.github.torbuntu.list.ListContainer;
import com.github.torbuntu.list.ListEntry;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.StageStyle;

/**
 *
 * @author tor
 */
public class Controller implements Initializable {

    @FXML
    private ListView lv;

    private ListContainer lc = new ListContainer(new ArrayList<ListEntry>());

    public void addItem(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setHeaderText(null);
        dialog.setTitle("Add New List");
        dialog.setContentText("List Title:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            DatabaseService.insertEntry(result.get());
            lv.getItems().clear();
            lc.getEntries().clear();
            lc = DatabaseService.selectEntries();
            setListView();
            System.out.println("Your new List Title: " + result.get());
        }

        // The Java 8 way to get the response value (with lambda expression).
        result.ifPresent(name -> System.out.println("Your name: " + name));

        System.out.println("EVENT: added item");
    }

    public void removeItem(ActionEvent actionEvent) {
        List<ListEntry> entries = lc.getEntries();
        int index = lv.getSelectionModel().getSelectedIndex();
        DatabaseService.deleteEntry(entries.get(index).getTitle());

        lv.getItems().remove(index);

        entries.remove(index);

        System.out.println("EVENT: removed item");
    }

    public void save(ActionEvent actionEvent) {
        DatabaseService.insertUpdateListEntries(lc);
    }

    private void setListView() {
        lc.getEntries().forEach(entry -> {
            lv.getItems().add(entry.getTitle());
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (DatabaseService.ensureListsTableExists()) {
            lc = DatabaseService.selectEntries();
            setListView();
        }
        if (DatabaseService.ensureItemsTableExists()) {
            System.out.println("success");
        }
    }
}
