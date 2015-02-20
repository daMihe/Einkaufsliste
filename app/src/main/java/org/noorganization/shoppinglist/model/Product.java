/*
 * Copyright 2015 Michael Wodniok
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  This file is part of Einkaufsliste.
 */

package org.noorganization.shoppinglist.model;

public class Product extends IdentificableModelObject{

    public String Title;
    public float  DefaultValue;
    public int    UnitId;

    public Product() {
        Id           = ModelManager.INVALID_ID;
        Title        = "";
        DefaultValue = 1.0f;
        UnitId       = ModelManager.INVALID_ID;
    }

    public Product(Product _toCopy) {
        Id           = _toCopy.Id;
        Title        = _toCopy.Title;
        DefaultValue = _toCopy.DefaultValue;
        UnitId       = _toCopy.UnitId;
    }
}
