"use client";

import React, { createContext, useContext, useState, ReactNode, useEffect, useCallback } from 'react';
import { Table, fetchTables } from '@/app/actions/table';

interface TableContextType {
  selectedTable: Table | null;
  tables: Table[];
  isLoading: boolean;
  setSelectedTable: (table: Table | null) => void;
  refreshTables: () => Promise<void>;
}

const TableContext = createContext<TableContextType | undefined>(undefined);

const TABLE_STORAGE_KEY = 'appetit-selected-table';

export function TableProvider({ children }: { children: ReactNode }) {
  const [selectedTable, setSelectedTableState] = useState<Table | null>(null);
  const [tables, setTables] = useState<Table[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const loadTables = useCallback(async () => {
    try {
      setIsLoading(true);
      const fetchedTables = await fetchTables();
      // Only show active tables
      const activeTables = fetchedTables.filter(t => t.active);
      setTables(activeTables);
      
      // Load saved table from localStorage
      const savedTableId = localStorage.getItem(TABLE_STORAGE_KEY);
      if (savedTableId) {
        const savedTable = activeTables.find(t => t.id === parseInt(savedTableId));
        if (savedTable) {
          setSelectedTableState(savedTable);
        } else if (activeTables.length > 0) {
          // If saved table not found, select first active table
          setSelectedTableState(activeTables[0]);
        }
      } else if (activeTables.length > 0) {
        // If no saved table, select first active table
        setSelectedTableState(activeTables[0]);
      }
    } catch (error) {
      console.error('Failed to load tables:', error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadTables();
  }, [loadTables]);

  const setSelectedTable = useCallback((table: Table | null) => {
    setSelectedTableState(table);
    if (table) {
      localStorage.setItem(TABLE_STORAGE_KEY, table.id.toString());
    } else {
      localStorage.removeItem(TABLE_STORAGE_KEY);
    }
  }, []);

  const refreshTables = useCallback(async () => {
    await loadTables();
  }, [loadTables]);

  return (
    <TableContext.Provider value={{
      selectedTable,
      tables,
      isLoading,
      setSelectedTable,
      refreshTables,
    }}>
      {children}
    </TableContext.Provider>
  );
}

export function useTable() {
  const context = useContext(TableContext);
  if (context === undefined) {
    throw new Error('useTable must be used within a TableProvider');
  }
  return context;
}

