/**
 * Copyright (C) 2009 Technische Universität Dresden
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

/**
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 */

package de.tudresden.inf.rn.mobilis.mxa.services.filetransfer;

import android.os.Messenger;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.FileTransfer;
import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileCallback;

interface IFileTransferService {

    /**
     * Sends a file to an XMPP entity.
     */
    void sendFile(in Messenger acknowledgement, in int requestCode, in FileTransfer file);
    
    /**
     * Registers a callback for incoming file transfers.
     */
    void registerFileCallback(de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileCallback cb);
    
    /**
     * Unregisters a previously registered callback for incoming file transfers.
     */
    void unregisterFileCallback(de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileCallback cb);
}