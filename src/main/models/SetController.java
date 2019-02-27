/*************************************************************************************************************************
 * Source Code Form License Notice
 *************************************************************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************/
/*************************************************************************************************************************
 *   Author: Jarek Thomas
 *
 *   Interface for SceneController to enforce a SetController method on all scene controllers
 ***************************************************************************************************************************/

package main.models;

import main.controllers.SceneController;

public interface SetController {
    
    public void setParentController(SceneController parentController);
    
}
