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
package com.github.torbuntu.list;

import java.util.List;

/**
 *
 * A List Entry is an entry in the overall collection of lists. The title is the
 * name of the List. ID is the primary key which the List Items will be linked
 * to.
 *
 * @author tor
 */
public class ListEntry {

    private int id;
    private String title;
    private List<ListItem> items;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ListItem> getItems() {
        return items;
    }

    public void setItems(List<ListItem> items) {
        this.items = items;
    }

    public ListEntry(String text) {
        this.title = text;
    }

    public ListEntry(int id, String text) {
        this.id = id;
        this.title = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
