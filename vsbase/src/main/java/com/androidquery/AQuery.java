/*
 * Copyright 2011 - AndroidQuery.com (tinyeeliu@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.androidquery;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.androidquery.util.AQueryConfig;
import com.vip.sdk.base.BaseApplication;

/**
 * The main class of AQuery. All methods are actually inherited from AbstractAQuery.
 */
public class AQuery extends AbstractAQuery<AQuery> {

    private static AQueryConfig config;

    public AQuery() {
        super(BaseApplication.getAppContext());
        init();
    }

    public AQuery(Activity act) {
        super(act);
        init();
    }

    public AQuery(View view) {
        super(view);
        init();
    }

    public AQuery(Context context) {
        super(context);
        init();
    }

    public AQuery(Activity act, View root) {
        super(act, root);
        init();
    }

    private void init() {
        if (config == null) {
            config = new AQueryConfig();
        }
    }
}


