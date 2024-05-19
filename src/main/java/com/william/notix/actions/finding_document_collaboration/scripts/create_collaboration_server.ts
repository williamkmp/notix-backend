import { Database } from "@hocuspocus/extension-database";
import { Server } from "@hocuspocus/server";
import { eq } from "drizzle-orm";
import { customType, pgTable, serial } from "drizzle-orm/pg-core";
import type { DatabaseConnection } from "./type";

const byteaType = customType<
    {
        data: Buffer,
        notNull: false,
        default: false 
    }
>(
    {
        dataType() {
            return "BYTEA";
        },
    }
);

const documentTable = pgTable('documents', {
    id: serial('id').primaryKey(),
    data: byteaType('data')
});

export function createCollaborationServer(
    param: 
    {
        database: DatabaseConnection,
        serverName: string,
        serverPort: number,
        updateDebounce: number,
        maxDebounceTime: number, 
    } 
) {
    const db = param.database;

    return Server.configure({
        name: param.serverName,
        port: param.serverPort,
        debounce: param.updateDebounce,
        maxDebounce: param.maxDebounceTime,
        extensions: [
            new Database({

                async fetch (data) {
                    const documentId = parseInt(data.documentName);
                    const resultSet = await db
                        .select()
                        .from(documentTable)
                        .where(eq(documentTable.id, documentId));
                    const documentRow = resultSet.at(0);
                    const documentData = documentRow?.data ?? null;
                    return documentData;
                },

                async store(data) {
                    const documentId = parseInt(data.documentName);
                    const documentData = data.state;
                    await db
                        .update(documentTable)
                        .set({data: documentData})
                        .where(eq(documentTable.id, documentId));
                },
            })
        ],
    
        async connected(data) {
            data.connection.requiresAuthentication = false;
        },
    });
}