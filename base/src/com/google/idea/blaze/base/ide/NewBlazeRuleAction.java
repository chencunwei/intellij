/*
 * Copyright 2016 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.idea.blaze.base.ide;

import com.google.idea.blaze.base.actions.BlazeAction;
import com.google.idea.blaze.base.experiments.ExperimentScope;
import com.google.idea.blaze.base.metrics.Action;
import com.google.idea.blaze.base.scope.Scope;
import com.google.idea.blaze.base.scope.scopes.BlazeConsoleScope;
import com.google.idea.blaze.base.scope.scopes.IdeaLogScope;
import com.google.idea.blaze.base.scope.scopes.IssuesScope;
import com.google.idea.blaze.base.scope.scopes.LoggedTimingScope;
import com.google.idea.blaze.base.settings.Blaze;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

class NewBlazeRuleAction extends BlazeAction implements DumbAware {

  public NewBlazeRuleAction() {
    super();
  }

  @Override
  public void actionPerformed(AnActionEvent event) {
    final Project project = event.getData(CommonDataKeys.PROJECT);
    if (project == null) {
      return;
    }
    final VirtualFile virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE);
    if (virtualFile == null) {
      return;
    }

    Scope.root(
        context -> {
          context
              .push(new ExperimentScope())
              .push(new BlazeConsoleScope.Builder(project).build())
              .push(new IssuesScope(project))
              .push(new IdeaLogScope())
              .push(new LoggedTimingScope(project, Action.CREATE_BLAZE_RULE));
          NewBlazeRuleDialog newBlazeRuleDialog =
              new NewBlazeRuleDialog(context, project, virtualFile);
          newBlazeRuleDialog.show();
        });
  }

  @Override
  protected void doUpdate(@NotNull AnActionEvent event) {
    Presentation presentation = event.getPresentation();
    DataContext dataContext = event.getDataContext();
    VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
    Project project = CommonDataKeys.PROJECT.getData(dataContext);
    boolean enabled =
        (project != null
            && file != null
            && Blaze.getBuildSystemProvider(project).isBuildFile(file.getName()));
    presentation.setVisible(enabled || ActionPlaces.isMainMenuOrActionSearch(event.getPlace()));
    presentation.setEnabled(enabled);
    presentation.setText(getText(project));
  }

  private static String getText(@Nullable Project project) {
    String buildSystem = Blaze.buildSystemName(project);
    return String.format("New %s Rule", buildSystem);
  }
}
