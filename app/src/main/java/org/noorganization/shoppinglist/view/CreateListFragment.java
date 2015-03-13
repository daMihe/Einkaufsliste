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
 *   This file is part of Einkaufsliste.
 */

package org.noorganization.shoppinglist.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.noorganization.shoppinglist.R;
import org.noorganization.shoppinglist.presenter.ShoppingListPresenter;

public class CreateListFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle _savedState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setTitle(R.string.new_list_enter_title);

        EditText titleTextView = new EditText(getActivity());
        titleTextView.setHint(R.string.title);
        titleTextView.setId(R.id.inner_text_view);
        dialogBuilder.setView(titleTextView);

        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface _dialogInterface, int button) {
                // Do nothing here - another listener will be added in onStart
            }
        });

        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface _dialogInterface, int i) {
            }
        });

        return dialogBuilder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    // Do here the real work.
                    String choosenTitle = ((EditText) dialog.findViewById(R.id.inner_text_view)).getText().toString();
                    if (choosenTitle.length() == 0) {
                        Toast.makeText(getActivity(), R.string.error_title_empty, Toast.LENGTH_LONG).show();
                    } else {
                        ShoppingListPresenter presenter = ShoppingListPresenter.getInstance(getActivity());
                        presenter.createList(choosenTitle);

                        ((MainActivity) getActivity()).updateListDropDown();

                        dismiss();
                    }
                }
            });
        }
    }

}
