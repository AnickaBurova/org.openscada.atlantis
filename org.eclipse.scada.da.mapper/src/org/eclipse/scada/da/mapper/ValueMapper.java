/*******************************************************************************
 * Copyright (c) 2012 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.scada.da.mapper;

import org.eclipse.scada.core.Variant;

public interface ValueMapper
{
    public Variant mapValue ( Variant value );

    public void addListener ( ValueMapperListener listener );

    public void removeListener ( ValueMapperListener listener );
}
